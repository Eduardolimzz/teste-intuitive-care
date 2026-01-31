import math
import time
from decimal import Decimal
from typing import Optional

from fastapi import APIRouter, Depends, Query, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import text, func

from app.database import get_db
from app.models import Operadora, DespesaConsolidada, DespesaAgregada
from app.schemas import (
    OperadoraResponse,
    DespesaResponse,
    EstatisticasResponse,
    TopOperadora,
    PaginatedResponse,
)

router = APIRouter()

# =====================================================
# Cache simples para /api/estatisticas
# Trade-off: Cache por 5 minutos
# Justificativa: dados mudam trimestralmente, não precisa ser tempo real
# =====================================================
_cache_estatisticas = {
    "data": None,
    "timestamp": 0,
}
CACHE_TTL_SEGUNDOS = 300  # 5 minutos


# =====================================================
# GET /api/operadoras - Listagem paginada
# Trade-off: Offset-based pagination
# Justificativa: dados estáticos, simples de implementar e manter
# =====================================================
@router.get("/operadoras", response_model=PaginatedResponse)
def listar_operadoras(
    page: int = Query(default=1, ge=1, description="Página atual"),
    limit: int = Query(default=10, ge=1, le=100, description="Itens por página"),
    busca: Optional[str] = Query(default=None, description="Filtro por razão social ou CNPJ"),
    db: Session = Depends(get_db),
):
    """Lista todas as operadoras com paginação e filtro opcional."""

    # Monta a query base
    query_total = db.query(func.count(Operadora.cnpj))
    query_dados = db.query(Operadora)

    # Se houver busca, aplica filtro (busca no servidor)
    # Trade-off: Busca no servidor (não no cliente)
    # Justificativa: evita carregar todos os dados no front
    if busca:
        filtro = f"%{busca}%"
        query_total = query_total.filter(
            Operadora.razao_social.like(filtro) | Operadora.cnpj.like(filtro)
        )
        query_dados = query_dados.filter(
            Operadora.razao_social.like(filtro) | Operadora.cnpj.like(filtro)
        )

    # Conta total de registros
    total = query_total.scalar()

    # Calcula offset e total de páginas
    offset = (page - 1) * limit
    total_pages = math.ceil(total / limit) if total > 0 else 1

    # Busca os dados com paginação
    operadoras = query_dados.offset(offset).limit(limit).all()

    # Converte para dicionários
    data = [
        OperadoraResponse.model_validate(op).model_dump()
        for op in operadoras
    ]

    return {
        "data": data,
        "total": total,
        "page": page,
        "limit": limit,
        "total_pages": total_pages,
    }


# =====================================================
# GET /api/operadoras/{cnpj} - Detalhes de uma operadora
# =====================================================
@router.get("/operadoras/{cnpj}", response_model=OperadoraResponse)
def detalhes_operadora(cnpj: str, db: Session = Depends(get_db)):
    """Retorna os detalhes de uma operadora específica pelo CNPJ."""
    operadora = db.query(Operadora).filter(Operadora.cnpj == cnpj).first()

    if not operadora:
        raise HTTPException(status_code=404, detail="Operadora não encontrada")

    return OperadoraResponse.model_validate(operadora)


# =====================================================
# GET /api/operadoras/{cnpj}/despesas - Histórico de despesas
# =====================================================
@router.get("/operadoras/{cnpj}/despesas")
def despesas_operadora(cnpj: str, db: Session = Depends(get_db)):
    """Retorna o histórico de despesas de uma operadora específica."""
    # Primeiro verifica se a operadora existe
    operadora = db.query(Operadora).filter(Operadora.cnpj == cnpj).first()
    if not operadora:
        raise HTTPException(status_code=404, detail="Operadora não encontrada")

    # Busca as despesas ordenadas por período
    despesas = (
        db.query(DespesaConsolidada)
        .filter(DespesaConsolidada.cnpj == cnpj)
        .order_by(DespesaConsolidada.ano, DespesaConsolidada.trimestre)
        .all()
    )

    data = [
        DespesaResponse.model_validate(d).model_dump()
        for d in despesas
    ]

    return {
        "cnpj": cnpj,
        "razao_social": operadora.razao_social,
        "despesas": data,
    }


# =====================================================
# GET /api/estatisticas - Estatísticas agregadas
# Trade-off: Cache por 5 minutos
# Justificativa: dados mudam trimestralmente, cache reduz carga no banco
# =====================================================
@router.get("/estatisticas", response_model=EstatisticasResponse)
def estatisticas(db: Session = Depends(get_db)):
    """Retorna estatísticas agregadas dos dados."""
    global _cache_estatisticas

    # Verifica se o cache ainda é válido
    agora = time.time()
    if _cache_estatisticas["data"] and (agora - _cache_estatisticas["timestamp"]) < CACHE_TTL_SEGUNDOS:
        return _cache_estatisticas["data"]

    # Calcula total de despesas
    total_result = db.query(func.coalesce(func.sum(DespesaConsolidada.valor_despesas), 0)).scalar()
    total_despesas = Decimal(str(total_result))

    # Calcula média de despesas por operadora
    total_operadoras = db.query(func.count(Operadora.cnpj)).scalar()
    media_despesas = total_despesas / total_operadoras if total_operadoras > 0 else Decimal("0")

    # Top 5 operadoras por total de despesas
    top5_query = (
        db.query(
            DespesaConsolidada.cnpj,
            DespesaConsolidada.razao_social,
            func.sum(DespesaConsolidada.valor_despesas).label("total"),
        )
        .group_by(DespesaConsolidada.cnpj, DespesaConsolidada.razao_social)
        .order_by(func.sum(DespesaConsolidada.valor_despesas).desc())
        .limit(5)
        .all()
    )

    top5 = [
        TopOperadora(cnpj=row.cnpj, razao_social=row.razao_social, total_despesas=Decimal(str(row.total)))
        for row in top5_query
    ]

    resultado = EstatisticasResponse(
        total_despesas=total_despesas,
        media_despesas=media_despesas.quantize(Decimal("0.01")),
        total_operadoras=total_operadoras,
        top5_operadoras=top5,
    )

    # Armazena no cache
    _cache_estatisticas["data"] = resultado
    _cache_estatisticas["timestamp"] = agora

    return resultado


# =====================================================
# GET /api/despesas-por-uf - Distribuição de despesas por UF
# Usado pelo gráfico da página principal
# =====================================================
@router.get("/despesas-por-uf")
def despesas_por_uf(db: Session = Depends(get_db)):
    """Retorna o total de despesas agrupado por UF, ordenado do maior para o menor."""
    resultado = (
        db.query(
            Operadora.uf.label("uf"),
            func.sum(DespesaConsolidada.valor_despesas).label("total_despesas"),
            func.count(func.distinct(DespesaConsolidada.cnpj)).label("qtd_operadoras"),
        )
        .join(DespesaConsolidada, Operadora.cnpj == DespesaConsolidada.cnpj)
        .filter(Operadora.uf.isnot(None))
        .filter(Operadora.uf != "")
        .group_by(Operadora.uf)
        .order_by(func.sum(DespesaConsolidada.valor_despesas).desc())
        .all()
    )

    data = [
        {
            "uf": row.uf,
            "total_despesas": str(Decimal(str(row.total_despesas))),
            "qtd_operadoras": row.qtd_operadoras,
        }
        for row in resultado
    ]

    return {"data": data}