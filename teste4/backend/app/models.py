from sqlalchemy import Column, String, Integer, Numeric
from app.database import Base

class Operadora(Base):
    __tablename__ = "operadoras"

    cnpj = Column(String(14), primary_key=True)
    razao_social = Column(String(255))
    uf = Column(String(2))


class DespesaConsolidada(Base):
    __tablename__ = "despesas_consolidadas"

    id = Column(Integer, primary_key=True, index=True)
    cnpj = Column(String(14))
    razao_social = Column(String(255))
    ano = Column(Integer)
    trimestre = Column(Integer)
    valor_despesas = Column(Numeric(15, 2))


class DespesaAgregada(Base):
    __tablename__ = "despesas_agregadas"

    id = Column(Integer, primary_key=True, index=True)
    razao_social = Column(String(255))
    uf = Column(String(2))
    total_despesas = Column(Numeric(15, 2))
