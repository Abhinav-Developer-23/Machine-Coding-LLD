from sqlalchemy.orm import Session
from app.models.product import Product
from app.schemas.product import ProductCreate, ProductUpdate

def get_product(db: Session, product_id: int):
    """Retrieve a single product by its unique ID."""
    return db.query(Product).filter(Product.id == product_id).first()

def get_products(db: Session, skip: int = 0, limit: int = 100):
    """Retrieve a paginated list of products."""
    return db.query(Product).offset(skip).limit(limit).all()

def create_product(db: Session, product: ProductCreate):
    """Create a new product record in the database."""
    db_product = Product(
        name=product.name,
        description=product.description,
        price=product.price,
        stock=product.stock
    )
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return db_product

def update_product(db: Session, product_id: int, product_update: ProductUpdate):
    """Update existing product fields using Pydantic model updates."""
    db_product = get_product(db, product_id)
    if not db_product:
        return None
    
    # model_dump(exclude_unset=True) is Pydantic v2 equivalent to dict(exclude_unset=True)
    update_data = product_update.model_dump(exclude_unset=True)
    for key, value in update_data.items():
        setattr(db_product, key, value)
        
    db.commit()
    db.refresh(db_product)
    return db_product

def delete_product(db: Session, product_id: int):
    """Delete a product by ID."""
    db_product = get_product(db, product_id)
    if not db_product:
        return None
    
    db.delete(db_product)
    db.commit()
    return db_product
