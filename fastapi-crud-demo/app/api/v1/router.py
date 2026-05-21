from fastapi import APIRouter
from app.api.v1.endpoints import products, downstream

api_router = APIRouter()

# Register endpoint groups with their corresponding path prefixes and Swagger tags.
api_router.include_router(products.router, prefix="/products", tags=["products"])
api_router.include_router(downstream.router, prefix="/downstream", tags=["downstream"])
