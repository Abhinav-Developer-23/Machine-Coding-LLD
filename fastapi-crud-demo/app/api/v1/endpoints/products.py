from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.crud import product as crud_product
from app.schemas import product as schema_product
from app.api.deps import get_db

router = APIRouter()

@router.post("/", response_model=schema_product.ProductResponse, status_code=status.HTTP_201_CREATED)
def create_product(product: schema_product.ProductCreate, db: Session = Depends(get_db)):
    """
    Create a new product record.
    """
    return crud_product.create_product(db=db, product=product)


@router.get("/", response_model=List[schema_product.ProductResponse])
def read_products(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    """
    Retrieve a paginated listing of all products.
    """
    return crud_product.get_products(db=db, skip=skip, limit=limit)


@router.get("/{product_id}", response_model=schema_product.ProductResponse)
def read_product(product_id: int, db: Session = Depends(get_db)):
    """
    Get detailed product profile by unique ID.
    """
    db_product = crud_product.get_product(db=db, product_id=product_id)
    if db_product is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Product with ID {product_id} not found."
        )
    return db_product


@router.put("/{product_id}", response_model=schema_product.ProductResponse)
def update_product(product_id: int, product_update: schema_product.ProductUpdate, db: Session = Depends(get_db)):
    """
    Update attributes of an existing product.
    """
    db_product = crud_product.update_product(db=db, product_id=product_id, product_update=product_update)
    if db_product is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Product with ID {product_id} not found."
        )
    return db_product


@router.delete("/{product_id}", response_model=schema_product.ProductResponse)
def delete_product(product_id: int, db: Session = Depends(get_db)):
    """
    Permanently delete a product record.
    """
    db_product = crud_product.delete_product(db=db, product_id=product_id)
    if db_product is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Product with ID {product_id} not found."
        )
    return db_product
