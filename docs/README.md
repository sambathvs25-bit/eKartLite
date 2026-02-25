# E-FlipkartLite Microservices

## Services
- `api-gateway` (8080): single entry point, JWT validation, role-based route protection
- `auth-service` (8081): customer registration, common login (customer/agent), JWT issuance
- `customer-service` (8082): customer profile APIs
- `product-service` (8083): product management, search, Redis cache, native SQL query
- `cart-service` (8084): cart and cart item management
- `order-service` (8085): order lifecycle and payment orchestration with circuit breaker
- `payment-service` (8086): mock payment processing (UPI/CARD)
- `batch-service` (8087): scheduled Spring Batch archival and daily sales report

## Mandatory stack implemented
- Maven multi-module project
- Spring Boot, Spring Data JPA, REST APIs
- Spring Security + JWT
- API Gateway (Spring Cloud Gateway)
- Redis caching in product search with eviction on updates
- Spring Batch scheduled job
- Circuit breaker: order -> payment
- MySQL datasource via one shared config file: `application-db.yml`

## One-file DB config
All DB and Redis details are centralized in root `application-db.yml` and imported in each service:
- `spring.config.import: optional:file:../application-db.yml`

## Database tables
Managed through JPA entities:
- `customer`, `agent`, `product`, `cart`, `cart_item`, `orders`, `order_item`, `payment`

## Native SQL query (mandatory)
In `product-service`:
- `ProductRepository#findAvailableProductsBelowPrice(...)`
- SQL: fetch available products below max price.

## Security model
- Roles: `ROLE_CUSTOMER`, `ROLE_AGENT`
- Gateway validates JWT and restricts route groups by role.
- Services also validate JWT as resource servers (defense in depth).
- Unsecured endpoints: `/auth/**`, `/actuator/health`

## One-command startup
1. Ensure Docker Desktop and Maven are installed.
2. From project root, run:
   - `powershell -ExecutionPolicy Bypass -File .\scripts\start-all.ps1`
3. To stop everything:
   - `powershell -ExecutionPolicy Bypass -File .\scripts\stop-all.ps1`

## What start-all does
- Starts MySQL + Redis via `docker compose`
- Waits for health checks
- Compiles project (unless `-SkipBuild` is provided)
- Starts all 8 Spring Boot services in separate PowerShell processes
- Stores running process IDs in `.running-services.json`

## Docker infra only
- Start infra only:
  - `docker compose up -d mysql redis`
- Stop infra only:
  - `docker compose stop mysql redis`

## Sample API flow (through gateway: `http://localhost:8080`)

### 1) Customer registration
`POST /auth/register`
```json
{
  "name": "Rahul",
  "email": "rahul@mail.com",
  "password": "rahul123",
  "mobileNumber": "9999999999"
}
```

### 2) Common login (customer or agent)
`POST /auth/login`
```json
{
  "email": "rahul@mail.com",
  "password": "rahul123"
}
```
Response:
```json
{
  "token": "<jwt>",
  "role": "CUSTOMER",
  "email": "rahul@mail.com"
}
```

### 3) Agent add/update products
`POST /agent/products`
```json
{
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 75000,
  "quantity": 12,
  "category": "Electronics",
  "status": "ACTIVE"
}
```
`PUT /agent/products/{id}/price`
```json
{ "price": 73000 }
```

### 4) Customer search products (cached)
`GET /customer/products/search?name=laptop&category=electronics`

### 5) Cart operations
`POST /customer/cart/items`
```json
{
  "productId": 1,
  "productName": "Laptop",
  "price": 73000,
  "quantity": 1
}
```
`GET /customer/cart`

### 6) Create order from cart-selected items
`POST /customer/orders`
```json
{
  "items": [
    {
      "productId": 1,
      "productName": "Laptop",
      "price": 73000,
      "quantity": 1
    }
  ]
}
```

### 7) Pay order (Circuit Breaker protected)
`POST /customer/orders/{orderId}/pay`
```json
{ "method": "UPI" }
```
Success response:
```json
{
  "orderId": 10,
  "paymentSuccess": true,
  "transactionReference": "TXN-...",
  "orderStatus": "READY_FOR_SHIPPING",
  "message": "Payment successful. Order ready for shipping"
}
```
Fallback response on payment-service failure:
```json
{
  "orderId": 10,
  "paymentSuccess": false,
  "transactionReference": "N/A",
  "orderStatus": "CREATED",
  "message": "Payment service unavailable. Please retry."
}
```

### 8) Agent order management
`GET /agent/orders/ready-for-shipping`

### 9) Batch run (manual trigger)
`POST /agent/batch/run`
- Also scheduled daily at 01:00 AM.
- Archives older READY_FOR_SHIPPING orders and logs daily sales report.

## Notes
- Default agent is seeded in auth service:
  - email: `agent@eflipkartlite.com`
  - password: `agent123`
- UI can use these APIs for login, dashboards, product/cart/order/payment/batch screens.
