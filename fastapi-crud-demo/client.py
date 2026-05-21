import json
import httpx

BASE_URL = "http://127.0.0.1:8000"

def run_client_demo():
    print("=" * 60)
    print("      FASTAPI CLIENT INTEGRATION DEMO (HTTPX)")
    print("=" * 60)

    # Initialize a synchronous HTTPX client
    with httpx.Client(base_url=BASE_URL) as client:
        
        # 1. CREATE (POST) a new product
        print("\n[1] Creating a new product...")
        new_product = {
            "name": "Mechanical Keyboard",
            "description": "Tactile switch RGB mechanical keyboard",
            "price": 89.99,
            "stock": 150
        }
        
        try:
            create_response = client.post("/products/", json=new_product)
            create_response.raise_for_status()
            product = create_response.json()
            product_id = product["id"]
            print(f"--> SUCCESS: Product created successfully!")
            print(json.dumps(product, indent=2))
        except httpx.HTTPError as exc:
            print(f"--> ERROR: Failed to create product: {exc}")
            return

        # 2. READ ALL (GET) products
        print("\n[2] Fetching list of all products...")
        try:
            list_response = client.get("/products/")
            list_response.raise_for_status()
            products = list_response.json()
            print(f"--> SUCCESS: Retrieved {len(products)} product(s)")
            print(json.dumps(products, indent=2))
        except httpx.HTTPError as exc:
            print(f"--> ERROR: Failed to fetch products: {exc}")

        # 3. UPDATE (PUT) the product details
        print(f"\n[3] Updating product with ID {product_id}...")
        update_payload = {
            "price": 79.99,  # Discount applied!
            "stock": 140
        }
        try:
            update_response = client.put(f"/products/{product_id}", json=update_payload)
            update_response.raise_for_status()
            updated_product = update_response.json()
            print(f"--> SUCCESS: Product updated successfully!")
            print(json.dumps(updated_product, indent=2))
        except httpx.HTTPError as exc:
            print(f"--> ERROR: Failed to update product: {exc}")

        # 4. READ ONE (GET) specific product
        print(f"\n[4] Querying updated product details for ID {product_id}...")
        try:
            get_response = client.get(f"/products/{product_id}")
            get_response.raise_for_status()
            fetched_product = get_response.json()
            print(f"--> SUCCESS: Retrieved product details:")
            print(json.dumps(fetched_product, indent=2))
        except httpx.HTTPError as exc:
            print(f"--> ERROR: Failed to query product: {exc}")

        # 5. DOWNSTREAM INTEGRATION (GET)
        todo_id = 1
        print(f"\n[5] Calling downstream integration API for Todo ID {todo_id}...")
        try:
            downstream_response = client.get(f"/downstream/todo/{todo_id}")
            downstream_response.raise_for_status()
            todo_data = downstream_response.json()
            print(f"--> SUCCESS: Retrieved data from downstream service:")
            print(json.dumps(todo_data, indent=2))
        except httpx.HTTPError as exc:
            print(f"--> ERROR: Downstream call failed: {exc}")

        # 6. DELETE (DELETE) the product
        print(f"\n[6] Deleting product with ID {product_id}...")
        try:
            delete_response = client.delete(f"/products/{product_id}")
            delete_response.raise_for_status()
            deleted_product = delete_response.json()
            print(f"--> SUCCESS: Product deleted successfully!")
            print(json.dumps(deleted_product, indent=2))
        except httpx.HTTPError as exc:
            print(f"--> ERROR: Failed to delete product: {exc}")

        # 7. CONFIRM DELETION
        print(f"\n[7] Verifying that product ID {product_id} is deleted...")
        try:
            verify_response = client.get(f"/products/{product_id}")
            if verify_response.status_code == 404:
                print(f"--> SUCCESS: Product is no longer available (404 Not Found as expected).")
            else:
                print(f"--> WARNING: Product was not deleted successfully (Status: {verify_response.status_code})")
        except httpx.HTTPError as exc:
            print(f"--> ERROR: Verification failed: {exc}")

    print("\n" + "=" * 60)
    print("      DEMO EXECUTION COMPLETE")
    print("=" * 60)


if __name__ == "__main__":
    # To run this client, ensure the FastAPI server is running in the background:
    # uvicorn app.main:app --reload --port 8000
    run_client_demo()
