import csv
import os
from decimal import Decimal

from sqlalchemy.orm import Session
from app.database import SessionLocal, engine, Base # Importe engine e Base
from app.models import Operadora, DespesaConsolidada, DespesaAgregada

# =====================================================
# Caminho base do projeto
# =====================================================
BASE_DIR = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../../../../")
)

DATA_DIR = os.path.join(BASE_DIR, "data", "output")


# =====================================================
# Importação das Operadoras
# Relatorio_cadop.csv (delimiter ;)
# =====================================================
def importar_operadoras(db: Session):
    path = os.path.join(DATA_DIR, "Relatorio_cadop.csv")

    with open(path, encoding="latin-1", newline="") as file:
        reader = csv.DictReader(file, delimiter=";")

        for row in reader:
            cnpj = row.get("CNPJ")

            if not cnpj:
                continue

            cnpj = cnpj.strip()

            if db.query(Operadora).filter_by(cnpj=cnpj).first():
                continue

            operadora = Operadora(
                cnpj=cnpj,
                razao_social=row.get("Razao_Social", "").strip(),
                uf=row.get("UF"),
            )

            db.add(operadora)

    db.commit()


# =====================================================
# Importação das Despesas Consolidadas
# consolidado_despesas.csv (delimiter ,)
# =====================================================
def importar_despesas(db: Session):
    path = os.path.join(DATA_DIR, "consolidado_despesas.csv")

    with open(path, encoding="utf-8", newline="") as file:
        reader = csv.DictReader(file, delimiter=",")

        print("COLUNAS DETECTADAS:", reader.fieldnames)

        for row in reader:
            try:
                cnpj = row.get("CNPJ")
                if not cnpj:
                    continue

                cnpj = cnpj.strip()

                ano = int(row.get("Ano", 0))
                trimestre = int(row.get("Trimestre", 0))

                # 🔥 Normalização do ano (12025 → 2025)
                if ano > 10000:
                    ano = ano - 10000

                if ano < 2000 or ano > 2100:
                    continue

                if trimestre < 1 or trimestre > 4:
                    continue

                valor_raw = row.get("ValorDespesas")
                if not valor_raw:
                    continue

                valor = Decimal(valor_raw)

                despesa = DespesaConsolidada(
                    cnpj=cnpj,
                    razao_social=row.get("RazaoSocial", "").strip(),
                    ano=ano,
                    trimestre=trimestre,
                    valor_despesas=valor,
                )

                db.add(despesa)

            except Exception as e:
                print("ERRO NA LINHA:", row)
                print("ERRO:", e)

    db.commit()


# =====================================================
# Execução principal
# =====================================================
if __name__ == "__main__":
    Base.metadata.create_all(bind=engine)

    db = SessionLocal()
    try:
        importar_operadoras(db)
        importar_despesas(db)
        print("Importação concluída com sucesso.")
    finally:
        db.close()
