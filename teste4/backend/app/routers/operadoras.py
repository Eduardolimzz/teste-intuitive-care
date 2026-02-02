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

_cache_estatisticas = {
    "data": None,
    "timestamp": 0,
}
CACHE_TTL_SEGUNDOS = 300


@router.get("/operadoras", response_model=PaginatedResponse)
def listar_operadoras(
    page: int = Query(default=1, ge=1, description="Página atual"),
    limit: int = Query(default=10, ge=1, le=100, description="Itens por página"),
    busca: Optional[str] = Query(default=None, description="Filtro por razão social ou CNPJ"),
    db: Session = Depends(get_db),
):
    query_total = db.query(func.count(Operadora.cnpj))
    query_dados = db.query(Operadora)

    if busca:
        filtro = f"%{busca}%"
        query_total = query_total.filter(
            Operadora.razao_social.like(filtro) | Operadora.cnpj.like(filtro)
        )
        query_dados = query_dados.filter(
            Operadora.razao_social.like(filtro) | Operadora.cnpj.like(filtro)
        )

    total = query_total.scalar()
    offset = (page - 1) * limit
    total_pages = math.ceil(total / limit) if total > 0 else 1

    operadoras = query_dados.offset(offset).limit(limit).all()

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


@router.get("/operadoras/{cnpj}", response_model=OperadoraResponse)
def detalhes_operadora(cnpj: str, db: Session = Depends(get_db)):
    operadora = db.query(Operadora).filter(Operadora.cnpj == cnpj).first()

    if not operadora:
        raise HTTPException(status_code=404, detail="Operadora não encontrada")

    return OperadoraResponse.model_validate(operadora)


@router.get("/operadoras/{cnpj}/despesas")
def despesas_operadora(cnpj: str, db: Session = Depends(get_db)):
    operadora = db.query(Operadora).filter(Operadora.cnpj == cnpj).first()
    if not operadora:
        raise HTTPException(status_code=404, detail="Operadora não encontrada")

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


@router.get("/estatisticas", response_model=EstatisticasResponse)
def estatisticas(db: Session = Depends(get_db)):
    global _cache_estatisticas

    agora = time.time()
    if _cache_estatisticas["data"] and (agora - _cache_estatisticas["timestamp"]) < CACHE_TTL_SEGUNDOS:
        return _cache_estatisticas["data"]

    total_result = db.query(func.coalesce(func.sum(DespesaConsolidada.valor_despesas), 0)).scalar()
    total_despesas = Decimal(str(total_result))

    total_operadoras = db.query(func.count(Operadora.cnpj)).scalar()
    media_despesas = total_despesas / total_operadoras if total_operadoras > 0 else Decimal("0")

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

    _cache_estatisticas["data"] = resultado
    _cache_estatisticas["timestamp"] = agora

    return resultado


@router.get("/despesas-por-uf")
def despesas_por_uf(db: Session = Depends(get_db)):
    resultado = (
        db.query(
            Operadora.uf.label("uf"),
            func.sum(DespesaConsolidada.valor_despesas).label("total_despesas"),
            func.count(func.distinct(DespesaConsolidada.registro_ans)).label("qtd_operadoras"),
        )
        .select_from(DespesaConsolidada)
        .join(Operadora, Operadora.registro_ans == DespesaConsolidada.registro_ans)
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