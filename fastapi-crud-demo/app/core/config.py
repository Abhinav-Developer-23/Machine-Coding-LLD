from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    PROJECT_NAME: str = "FastAPI Enterprise Backend"
    API_V1_STR: str = "/api/v1"
    
    # Defaults to local SQLite, but can be overridden by environment variables in prod.
    DATABASE_URL: str = "sqlite:///./products.db"

    class Config:
        case_sensitive = True
        # Reads from a .env file in the current working directory if it exists
        env_file = ".env"

settings = Settings()
