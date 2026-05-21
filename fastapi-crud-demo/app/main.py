from fastapi import FastAPI
from app.core.config import settings
from app.core.database import engine, Base
from app.api.v1.router import api_router

# Automatically construct SQLite tables during application bootstrapping.
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title=settings.PROJECT_NAME,
    description="Enterprise-grade production-ready FastAPI layout featuring isolated core configs, models, routers, and layers.",
    version="1.0.0",
)

# Connect the structured V1 router hierarchy to the root FastAPI application instance.
app.include_router(api_router)
