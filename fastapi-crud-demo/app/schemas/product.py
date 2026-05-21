from datetime import datetime
from typing import Optional
from pydantic import BaseModel, Field, ConfigDict

# Base schema containing shared attributes for products.
class ProductBase(BaseModel):
    name: str = Field(..., min_length=1, max_length=100, description="Name of the product")
    description: Optional[str] = Field(None, description="Detailed description of the product")
    price: float = Field(..., gt=0, description="Price must be greater than zero")
    stock: int = Field(0, ge=0, description="Stock must be non-negative")

# Schema for creating a product (inherits all attributes).
class ProductCreate(ProductBase):
    pass

# Schema for updating a product (all attributes are optional).
class ProductUpdate(BaseModel):
    name: Optional[str] = Field(None, min_length=1, max_length=100)
    description: Optional[str] = None
    price: Optional[float] = Field(None, gt=0)
    stock: Optional[int] = Field(None, ge=0)

# Schema for response serialization (adds db-generated fields).
class ProductResponse(ProductBase):
    id: int
    created_at: datetime

    # Pydantic v2 configuration to read ORM objects seamlessly
    model_config = ConfigDict(from_attributes=True)
