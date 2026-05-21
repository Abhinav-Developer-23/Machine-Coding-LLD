# FastAPI CRUD Backend & Client (Enterprise Nested Layout)

A highly structured, production-ready Python backend template using **FastAPI**, **SQLAlchemy ORM** (connected to SQLite), and **Pydantic v2** validation. 

This repository implements the **Enterprise Nested Package Layout**, decoupling routing, business layers, configuration systems, and data controllers. It also provides a standalone HTTPX client demonstrating end-to-end service interactions.

---

## 🛠️ Architecture & Components

The application is structured into domain packages to separate concerns and prevent circular dependencies:

```
fastapi-crud-demo/
├── requirements.txt            # Core dependencies (FastAPI, SQLAlchemy, Pydantic, HTTPX, Uvicorn)
├── app/                        # Main application package
│   ├── __init__.py             # Exposes app folder as package
│   ├── main.py                 # Application bootstrap and router registration
│   ├── core/                   # Central configuration & database management
│   │   ├── config.py           # Environment validations via Pydantic Settings
│   │   └── database.py         # SQLAlchemy engine setup & SessionLocal base
│   ├── models/                 # Database ORM classes
│   │   └── product.py          # Product table schema
│   ├── schemas/                # Pydantic validation and typing layers
│   │   └── product.py          # Request bodies and serialized serializers
│   ├── crud/                   # Pure database query controller actions
│   │   └── product.py          # CRUD query and mutate transaction routines
│   ├── services/               # Downstream microservice helper engines
│   │   └── downstream.py       # Asynchronous HTTPX downstream connector calls
│   └── api/                    # System endpoints definitions
│       ├── deps.py             # Route dependency suppliers (get_db session yields)
│       └── v1/                 # Version 1 Router namespace
│           ├── router.py       # Collects and registers endpoint groups
│           └── endpoints/      # Domain specific route files
│               ├── products.py   # Product CRUD endpoints
│               └── downstream.py # Todo external downstream proxy route
└── client.py                   # Standalone client script demonstrating end-to-end interactions
```

---

## 🔌 API Documentation

All routes behave exactly the same way to maintain total client backward compatibility:

### Database CRUD Endpoints (HTTP Methods)

| Endpoint | HTTP Method | Description |
| :--- | :--- | :--- |
| `/products/` | `POST` | Create a new product. Accepts JSON matching `ProductCreate` schema. |
| `/products/` | `GET` | Paginated query for all products. Supports `skip` and `limit` query params. |
| `/products/{id}` | `GET` | Get details of a specific product. |
| `/products/{id}` | `PUT` | Partially or fully update an existing product. |
| `/products/{id}` | `DELETE` | Delete a product from the database. |

### Downstream Integration Endpoint

| Endpoint | HTTP Method | Description |
| :--- | :--- | :--- |
| `/downstream/todo/{id}` | `GET` | Asynchronously contacts an external REST API using `httpx.AsyncClient` via the dedicated `services/` namespace. |

---

## 🚀 How to Run the Project (For Reference)

If you decide to execute the code in the future:

### 1. Set up a Virtual Environment
Create a clean environment to manage dependencies:
```bash
python -m venv venv
venv\Scripts\activate     # On Windows
source venv/bin/activate  # On macOS/Linux
```

### 2. Install Dependencies
```bash
pip install -r requirements.txt
```

### 3. Launch the Backend Server
Start the ASGI server using Uvicorn targeting the package main file:
```bash
uvicorn app.main:app --reload
```
Once started, the backend will be available at **`http://127.0.0.1:8000`**.
- Interactive Swagger UI: [http://127.0.0.1:8000/docs](http://127.0.0.1:8000/docs)
- Alternative ReDoc documentation: [http://127.0.0.1:8000/redoc](http://127.0.0.1:8000/redoc)

### 4. Run the Client Integration Script
In a separate terminal (with the virtual environment active), run:
```bash
python client.py
```
This script will sequentially execute creation, retrieval, updates, downstream call, and deletion routines.
