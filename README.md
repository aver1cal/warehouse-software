# Warehouse Backend

This Spring Boot backend service manages products, articles inventory, and handles orders for a warehouse system.
Backend uses postgreSQL db, can be run locally via docker container and deployed to kubernetes with helm.

## API Endpoints

### Inventory

- **GET /api/v1/inventory**
  - Returns a list of all inventory items.
  - **Response Example:**
    ```json
    [
      { "id": 1, "name": "leg", "stock": 12 },
      { "id": 2, "name": "screw", "stock": 17 }
    ]
    ```

### Products

- **GET /api/v1/products**
  - Returns a list of all products and available stock.
  - **Response Example:**
    ```json
    [
      {
        "id": 1,
        "name": "Dining Chair",
        "contain_articles": [
          { "art_id": 1, "amount_of": 4 },
          { "art_id": 2, "amount_of": 8 }
        ],
        "price": 20,
        "stock": 2
      }
    ]
    ```

### Orders

- **POST /api/v1/orders**
  - Places a new order.
  - **Request Example:**
    ```json
    {
      "customerName": "John Doe",
      "orderDate": "2025-06-28",
      "products": [
        { "productId": 1, "quantity": 1 },
        { "productId": 2, "quantity": 1 },
      ]
    }
    ```
  - **Response Example (success):**
    ```json
    {
      "id": 10,
      "customerName": "John Doe",
      "orderDate": "2025-06-28",
      "status": "PROCESSED"
    }
    ```

- **GET /api/v1/orders/{id}**
  - Returns details for a specific order.

---

## Docker

You can run the backend locally as a Docker container or build an image.

**Build the image:**
```sh
./build-docker.sh IMAGE_TAG
```

**Run the container locally:**
```sh
docker compose up
```

- The application will be available at `http://localhost:8080`.
- Make sure your database is accessible to the container, or use Docker Compose to run both the backend and the database together.

## Helm

You can deploy the backend to Kubernetes using Helm.

**Deploy with Helm:**
```sh
./helm-deploy.sh IMAGE_TAG
```

- You can customize deployment settings in `helm/values.yaml` before deploying.

## Importing data on startup

On application startup, the backend automatically imports initial inventory and product data from JSON files located in `src/main/resources/data/`:

- **Inventory:** `data/inventory.json`
- **Products:** `data/products.json`

**How it works:**
- The importers run at startup and populate the database with inventory items and products.
- If an item or product already exists (matched by ID), it will be updated with the data from the JSON file.
- If it does not exist, it will be inserted as a new record.

## Testing
You can run unit and integration tests using Maven:

```sh
./mvnw test
```