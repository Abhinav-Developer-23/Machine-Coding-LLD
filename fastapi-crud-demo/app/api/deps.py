from typing import Generator
from app.core.database import SessionLocal

def get_db() -> Generator:
    """
    Endpoint Dependency: Yields an active SQLAlchemy Session.
    Guarantees session closing when execution completes.
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
