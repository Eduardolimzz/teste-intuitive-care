from fastapi import FastAPI
from sqlalchemy import text
from app.database import engine
from app.routers.operadoras import router

app = FastAPI(title="Teste 4 - Intuitive Care")

@app.get("/api/health")
def health_check():
    try:
        with engine.connect() as conn:
            conn.execute(text("SELECT 1"))
        return {"status": "ok", "database": "mysql"}
    except Exception as e:
        return {"status": "error", "detail": str(e)}

app.include_router(router, prefix="/api")
