# pharmacy-microservices

Java 21 / Spring Boot microservices project for pharmacy operations, aligned with the current implementation.

## Architecture overview
- **Gateway (`gateway`)**: single entry point for routed APIs.
- **Sales Service (`sales-service`)**: registers sales and publishes events.
- **Inventory Service (`inventory-service`)**: manages products and stock; consumes sales events to decrease stock.
- **Expiration Service (`expiration-service`)**: tracks product expiration status and scheduled expiration checks.
- **Kafka**: asynchronous communication through `sale-created` (sales → inventory) and `product-created` (inventory → expiration).

## Service responsibilities
- **gateway**: route external requests to internal services.
- **sales-service**: persist sales (`H2`) and emit `sale-created` events.
- **inventory-service**: CRUD/read/update stock (`H2`), react to `sale-created`, and publish `product-created` on product creation.
- **expiration-service**: maintain expiration records (`H2`), auto-sync from `product-created`, list expired products, and run periodic checks.

## Ports and routes
| Component | Port | Base route | Notes |
|---|---:|---|---|
| gateway | 8080 | `/sales/**`, `/products/**` | Routes to sales and inventory |
| sales-service | 8081 | `/sales` | Also reachable via gateway `/sales/**` |
| inventory-service | 8082 | `/products` | Also reachable via gateway `/products/**` |
| expiration-service | 8083 | `/api/expirations/products` | Also reachable via gateway `/api/expirations/**` |
| Kafka broker | 9092 | topics `sale-created`, `product-created` | Used by service integrations |

## Kafka event flow (`sale-created`)
1. Client creates a sale in **sales-service** (`POST /sales`).
2. sales-service persists the sale and publishes `sale-created` (`saleId`, `productId`, `quantity`).
3. **inventory-service** listens to `sale-created` and decrements product stock.
4. Invalid events (e.g., product not found, insufficient stock) are logged and ignored by consumer logic.

## Kafka event flow (`product-created`)
1. Client creates a product in **inventory-service** (`POST /products`).
2. inventory-service persists the product and publishes `product-created` (`productId`, `name`, `expirationDate`).
3. **expiration-service** listens to `product-created` and auto-creates/updates expiration tracking (`productCode=INV-{productId}`).

## Main endpoints by service
### Gateway (8080)
- `POST /sales`
- `GET /sales`
- `GET /sales/{id}`
- `POST /products`
- `GET /products`
- `GET /products/{id}`
- `PUT /products/{id}/stock`

### Sales Service (8081)
- `POST /sales`
- `GET /sales`
- `GET /sales/{id}`

### Inventory Service (8082)
- `POST /products`
- `GET /products`
- `GET /products/{id}`
- `PUT /products/{id}/stock`

### Expiration Service (8083)
- `GET /api/expirations/products`
- `GET /api/expirations/products/expired`
- `POST /api/expirations/products`

## Running with Docker Compose
From project root:

```bash
docker compose up
```

Expected runtime: gateway, services, and Kafka available together.

Basic usage sequence:
1. Create product (`POST /products` via gateway).
2. Create sale (`POST /sales` via gateway).
3. Confirm stock update (`GET /products/{id}`).
4. (Optional) Add/list expirations via expiration-service endpoints.

## Scope notes
### In scope
- API gateway routing (sales/inventory).
- Sales registration and event publication.
- Inventory management and stock decrement via Kafka.
- Product expiration tracking and expired-status processing.

### Out of scope
- Authentication/authorization.
- Payments/billing processing.
- Distributed transactions/Saga orchestration.
- External notification channels (email/SMS).
- Production-grade observability and hardening.
