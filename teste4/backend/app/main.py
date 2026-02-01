from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import text

from app.database import engine
from app.models import Base   # 👈 ADICIONADO
from app.routers.operadoras import router

app = FastAPI(title="Teste 4 - Intuitive Care")

# =====================================================
# CRIA AS TABELAS CONTROLADAS PELO SQLALCHEMY
# (sem alterar nada existente)
# =====================================================
Base.metadata.create_all(bind=engine)

# =====================================================
# CORS
# Permite frontend Vue consumir a API
# =====================================================
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/api/health")
def health_check():
    try:
        with engine.connect() as conn:
            conn.execute(text("SELECT 1"))
        return {"status": "ok", "database": "mysql"}
    except Exception as e:
        return {"status": "error", "detail": str(e)}

app.include_router(router, prefix="/api")
