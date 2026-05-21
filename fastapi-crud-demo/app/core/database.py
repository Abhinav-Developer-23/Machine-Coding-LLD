from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from app.core.config import settings

# Check if we are using SQLite, and if so, enable multi-threaded connection arguments.
connect_args = {"check_same_thread": False} if settings.DATABASE_URL.startswith("sqlite") else {}

# Create ORM database engine utilizing connection URL from centralized config.
engine = create_engine(
    settings.DATABASE_URL, connect_args=connect_args
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()
