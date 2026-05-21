from datetime import datetime
from sqlalchemy import Column, Integer, String, Float, DateTime
from app.core.database import Base

class Product(Base):
    __tablename__ = "products"

    id = Column(Integer, primary key=True, index=True)
    name = Column(String, index=True, nullable=False)
    description = Column(String, nullable=True)
    price = Column(Float, nullable=False)
    stock = Column(Integer, default=0)
    created_at = Column(DateTime, default=datetime.utcnow)
