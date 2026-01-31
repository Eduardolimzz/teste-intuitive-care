from pydantic import BaseModel
from decimal import Decimal
from typing import List, Optional


class OperadoraResponse(BaseModel):
    cnpj: str
    razao_social: str
    uf: Optional[str]

    class Config:
        from_attributes = True


class DespesaResponse(BaseModel):
    ano: int
    trimestre: int
    valor_despesas: Decimal

    class Config:
        from_attributes = True


class TopOperadora(BaseModel):
    cnpj: str
    razao_social: str
    total_despesas: Decimal


class EstatisticasResponse(BaseModel):
    total_despesas: Decimal
    media_despesas: Decimal
    total_operadoras: int
    top5_operadoras: List[TopOperadora]


class PaginatedResponse(BaseModel):
    data: list
    total: int
    page: int
    limit: int
    total_pages: int
