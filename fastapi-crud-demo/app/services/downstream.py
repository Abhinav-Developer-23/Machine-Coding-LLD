import httpx

async def fetch_downstream_todo(todo_id: int) -> dict:
    """
    Asynchronously queries an external downstream service for a specific Todo resource.
    Returns the JSON representation of the downstream payload.
    """
    external_url = f"https://jsonplaceholder.typicode.com/todos/{todo_id}"
    
    # Utilizing HTTPX AsyncClient inside services for optimized non-blocking async network calls.
    async with httpx.AsyncClient() as client:
        response = await client.get(external_url, timeout=5.0)
        response.raise_for_status()
        return response.json()
