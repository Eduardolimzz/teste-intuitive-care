import csv
import os
from decimal import Decimal

from sqlalchemy.orm import Session
from app.database import SessionLocal
from app.models import Operadora, DespesaConsolidada

# =====================================================
# Caminho base do projeto (raiz do repositório)
# =====================================================
BASE_DIR = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../../../../")
)

DATA_DIR = os.path.join(BASE_DIR, "data", "output")


# =====================================================
# Importação das Operadoras (Relatorio_cadop.csv)
# Delimitador: ;
# =====================================================
def importar_operadoras(db: Session):
    path = os.path.join(DATA_DIR, "Relatorio_cadop.csv")

    with open(path, encoding="latin-1") as file:
        reader = csv.DictReader(file, delimiter=";")

        for row in reader:
            cnpj = row["CNPJ"].strip()

            # Validação mínima de CNPJ
            if not cnpj.isdigit() or len(cnpj) != 14:
                continue

            # Evita duplicação
            existe = db.query(Operadora).filter_by(cnpj=cnpj).first()
            if existe:
                continue

            operadora = Operadora(
                cnpj=cnpj,
                razao_social=row["Razao_Social"].strip(),
                uf=row["UF"].strip() if row.get("UF") else None,
            )

            db.add(operadora)

    db.commit()


# =====================================================
# Importação das Despesas Consolidadas
# consolidado_despesas.csv
# Delimitador: ,
# =====================================================
def importar_despesas(db: Session):
    path = os.path.join(DATA_DIR, "consolidado_despesas.csv")

    with open(path, encoding="utf-8") as file:
        reader = csv.DictReader(file, delimiter=",")

        for row in reader:
            try:
                cnpj = row["CNPJ"].strip()

                # Ignora linhas que não representam operadoras
                if not cnpj.isdigit() or len(cnpj) != 14:
                    continue

                ano = int(row["Ano"])
                trimestre = int(row["Trimestre"])

                # Replica constraints do banco (Teste 3)
                if ano < 2000 or ano > 2100:
                    continue
                if trimestre < 1 or trimestre > 4:
                    continue

                valor = (
                    row["ValorDespesas"]
                    .replace(".", "")
                    .replace(",", ".")
                )

                despesa = DespesaConsolidada(
                    cnpj=cnpj,
                    razao_social=row["RazaoSocial"].strip(),
                    ano=ano,
                    trimestre=trimestre,
                    valor_despesas=Decimal(valor),
                )

                # merge garante idempotência
                db.merge(despesa)

            except Exception:
                # Qualquer linha inválida é ignorada
                continue

    db.commit()


# =====================================================
# Execução principal
# =====================================================
if __name__ == "__main__":
    db = SessionLocal()
    try:
        importar_operadoras(db)
        importar_despesas(db)
        print("Importação concluída com sucesso.")
    finally:
        db.close()
