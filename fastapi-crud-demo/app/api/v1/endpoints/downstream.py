from fastapi import APIRouter, HTTPException, status
import httpx
from app.services.downstream import fetch_downstream_todo

router = APIRouter()

@router.get("/todo/{todo_id}")
async def get_downstream_todo(todo_id: int):
    """
    Fetch Todo record from external downstream server using the service utility.
    """
    try:
        todo_data = await fetch_downstream_todo(todo_id=todo_id)
        return {
            "message": "Downstream call successful",
            "source": "https://jsonplaceholder.typicode.com",
            "data": todo_data
        }
    except httpx.HTTPStatusError as exc:
        raise HTTPException(
            status_code=exc.response.status_code,
            detail=f"Downstream service returned error: {exc.response.text}"
        )
    except httpx.RequestError as exc:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail=f"An error occurred while contacting downstream service: {exc}"
        )
