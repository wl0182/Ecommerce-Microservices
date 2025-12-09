# E-Commerce Microservices Platform

A production-ready microservices-based e-commerce platform built with Spring Boot, featuring event-driven architecture, service discovery, API gateway, and comprehensive resilience patterns.

##  Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Services](#services)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Event-Driven Communication](#event-driven-communication)
- [Resilience Patterns](#resilience-patterns)
- [Domain-Driven Design](#domain-driven-design)
- [Contributing](#contributing)

---

##  Overview

This platform demonstrates a complete microservices implementation for an e-commerce system, handling user management, product catalog, shopping cart, order processing, payments, shipping, and notifications through loosely-coupled, independently deployable services.

### Key Features

- **Microservices Architecture**: 9 independent services with clear bounded contexts
- **Service Discovery**: Eureka server for dynamic service registration and discovery
- **API Gateway**: Centralized routing, authentication, and rate limiting
- **Event-Driven**: Kafka-based asynchronous communication between services
- **Resilience**: Circuit breakers, retries, rate limiting, and bulkheads via Resilience4j
- **RESTful APIs**: Comprehensive REST endpoints for all business operations
- **Domain-Driven Design**: Clear separation of concerns and bounded contexts

---

##  Architecture

### High-Level Architecture

```
┌─────────────┐
│ API Gateway │ :7000
└──────┬──────┘
       │
       ├──────┬──────┬──────┬──────┬──────┬──────┬──────┐
       │      │      │      │      │      │      │      │
    ┌──▼──┐ ┌─▼─┐ ┌──▼──┐ ┌─▼──┐ ┌─▼──┐ ┌─▼──┐ ┌─▼──┐ ┌─▼──┐
    │User │ │Prod││Cart │ │Order││Pay │ │Ship││Notif││Eureka│
    │:7001│ │:7002││:7003│ │:7004││:7005│ │:7006││:7007││:8761│
    └─────┘ └────┘ └─────┘ └─────┘ └────┘ └────┘ └────┘ └─────┘
                         │
                         │ Kafka Event Bus
                         └────────────────────────
```

### Service Ports

| Service | Port |
|---------|------|
| API Gateway | 7000 |
| User Service | 7001 |
| Product Service | 7002 |
| Cart Service | 7003 |
| Order Service | 7004 |
| Payment Service | 7005 |
| Shipping Service | 7006 |
| Notification Service | 7007 |
| Eureka Server | 8761 |

---

## ️ Technology Stack

- **Framework**: Spring Boot 3.x
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Messaging**: Apache Kafka
- **Database**: PostgreSQL
- **Resilience**: Resilience4j (Circuit Breaker, Retry, Rate Limiter, Bulkhead)
- **Build Tool**: Maven
- **Java Version**: 17+

---

##  Services

### 1. User Service (Identity & Access Context)
**Port**: 7001

Manages user accounts, authentication, and addresses.

**Key Endpoints**:
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User authentication
- `GET /api/users/{id}` - Get user details
- `PUT /api/users/{id}` - Update user profile
- `GET /api/users/{id}/addresses` - List user addresses
- `POST /api/users/{id}/addresses` - Add new address
- `GET /api/users/{userId}/addresses/{addressId}/validate` - Validate address

**Events Produced**: `user-registered`

---

### 2. Product Service (Catalog & Inventory Context)
**Port**: 7002

Manages product catalog, categories, and inventory.

**Key Endpoints**:
- `GET /products` - List all products
- `GET /products/{id}` - Get product details
- `POST /products` - Create new product
- `PATCH /products/{id}` - Update product
- `POST /products/bulk` - Bulk get products by IDs
- `PUT /products/{id}/increment-stock` - Increase inventory
- `PUT /products/{id}/decrement-stock` - Decrease inventory
- `GET /products/{id}/inventory` - Get inventory level
- `GET /categories/all` - List categories
- `POST /categories/create` - Create category
- `GET /categories/{id}` - Get category details
- `PUT /categories/{id}` - Update category
- `DELETE /categories/{id}` - Delete category
- `GET /categories/{id}/products` - Products by category

**Events Produced**: `product-updated`  
**Events Consumed**: `order-cancelled`

---

### 3. Cart Service (Shopping Context)
**Port**: 7003

Manages user shopping carts and checkout process.

**Key Endpoints**:
- `POST /cart/{userId}/items` - Add item to cart
- `GET /cart/{userId}` - View cart
- `DELETE /cart/{userId}/items/{productId}` - Remove item
- `DELETE /cart/{userId}` - Clear cart
- `POST /cart/{userId}/checkout` - Checkout cart

**External Calls**:
- Product Service (get product details)
- Order Service (create order)

**Events Produced**: `cart-cleared`  
**Events Consumed**: `product-updated`

---

### 4. Order Service (Order Management Context)
**Port**: 7004

Central orchestrator for order lifecycle management.

**Key Endpoints**:
- `POST /api/orders/place-order` - Create new order
- `GET /api/orders/{orderId}` - Get order details
- `GET /api/orders/user/{userId}` - List user orders
- `PUT /api/orders/cancel/{orderId}` - Cancel order
- `GET /api/orders/status/{orderId}` - Get order status
- `GET /api/orders/product/{productId}` - Get product details
- `GET /stats/top-products` - Top-selling products

**External Calls**:
- Product Service (inventory management)

**Events Produced**: `order-created`, `order-cancelled`, `order-paid`  
**Events Consumed**: `payment-processed`, `payment-failed`, `payment-refunded`, `product-updated`

---

### 5. Payment Service (Payment Context)
**Port**: 7005

Handles payment processing and refunds.

**Key Endpoints**:
- `GET /api/payments/{paymentId}` - Get payment details
- `GET /api/payments/order/{orderId}` - Get payments by order
- `POST /api/payments/refund` - Issue refund

**Events Produced**: `payment-processed`, `payment-failed`, `payment-refunded`, `order-paid`  
**Events Consumed**: `order-created`, `order-cancelled`

**Resilience**: Circuit breaker for external payment gateway (95/5 success/failure simulation)

---

### 6. Shipping Service (Logistics Context)
**Port**: 7006

Manages shipment creation and tracking.

**Key Endpoints**:
- `POST /shipments/ship/order/{orderId}` - Create shipment
- `GET /shipments/{id}` - Get shipment details
- `GET /shipments/order/{orderId}` - Get shipment by order

**External Calls**:
- User Service (get delivery address)

**Events Produced**: `shipment-created`, `shipment-updated`  
**Events Consumed**: `order-paid`

---

### 7. Notification Service (Communication Context)
**Port**: 7007

Sends transactional emails and SMS notifications.

**Key Endpoints**:
- `GET /notifications/{id}` - Get notification details
- `GET /notifications/user/{userId}` - List user notifications
- `POST /notifications/sendTestEmail` - Test email functionality

**Events Consumed**: `user-registered`, `order-created`, `payment-processed`, `payment-refunded`, `shipment-created`, `shipment-updated`, `order-paid`, `order-cancelled`

---

### 8. API Gateway
**Port**: 7000

Routes requests, handles authentication, and applies rate limiting.

**Features**:
- Request routing to backend services
- Authentication and authorization
- Rate limiting
- Load balancing

---

### 9. Eureka Service Discovery
**Port**: 8761

Service registry for dynamic service discovery.

**Dashboard**: `http://localhost:8761`

---

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 14+
- Apache Kafka
- Docker (optional, for running dependencies)

### Database Setup

```bash
# Start PostgreSQL
brew services start postgresql@14

# Create databases for each service
createdb user_service_db
createdb product_service_db
createdb cart_service_db
createdb order_service_db
createdb payment_service_db
createdb shipping_service_db
createdb notification_service_db
```

### Running Services

#### Option 1: Using Shell Script (Recommended)

```bash
# Make script executable
chmod +x run-all.sh

# Run all services
./run-all.sh
```

#### Option 2: Manual Start (Development)

```bash
# Start Eureka Server first
mvn -pl Eureka-Server spring-boot:run

# Start API Gateway
mvn -pl api-gateway spring-boot:run

# Start individual services
mvn -pl user-service spring-boot:run
mvn -pl product-service spring-boot:run
mvn -pl Cart-service spring-boot:run
mvn -pl order-service spring-boot:run
mvn -pl Payment-service spring-boot:run
mvn -pl Shipping-service spring-boot:run
mvn -pl notification-service spring-boot:run
```

### Verify Services

```bash
# Check Eureka dashboard
open http://localhost:8761

# Check if all services are registered
curl http://localhost:8761/eureka/apps

# Test API Gateway
curl http://localhost:7000/actuator/health
```

---

##  API Documentation

### Complete Flow Example: Order Placement

```bash
# 1. Register User
curl -X POST http://localhost:7000/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "secure123"
  }'

# 2. Add Products to Cart
curl -X POST http://localhost:7000/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 101,
    "quantity": 2
  }'

# 3. Add Shipping Address
curl -X POST http://localhost:7000/api/users/1/addresses \
  -H "Content-Type: application/json" \
  -d '{
    "street": "123 Main St",
    "city": "New York",
    "zip": "10001",
    "country": "USA",
    "isDefault": true
  }'

# 4. Checkout Cart
curl -X POST http://localhost:7000/cart/1/checkout \
  -H "Content-Type: application/json" \
  -d '{
    "paymentMethod": "CARD",
    "addressId": 1
  }'

# 5. Track Order Status
curl http://localhost:7000/api/orders/status/1001

# 6. Track Shipment
curl http://localhost:7000/shipments/order/1001
```

For detailed API documentation, see [Architecture.md](./Architecture.md) or [Architecture.html](./Architecture.html).

---

##  Event-Driven Communication

### Kafka Topics

| Topic | Producer | Consumers | Purpose |
|-------|----------|-----------|---------|
| `user-registered` | User Service | Notification Service | Welcome email/SMS |
| `product-updated` | Product Service | Cart, Order Services | Product/price changes |
| `order-created` | Order Service | Payment, Notification Services | Payment trigger, confirmation |
| `order-paid` | Payment Service | Shipping, Notification, Order Services | Shipment trigger |
| `order-cancelled` | Order Service | Product, Payment, Notification Services | Stock restore, refund |
| `payment-processed` | Payment Service | Order, Notification Services | Status update |
| `payment-failed` | Payment Service | Order, Notification Services | Failure handling |
| `payment-refunded` | Payment Service | Notification, Order Services | Refund confirmation |
| `shipment-created` | Shipping Service | Notification Service | Shipment notification |
| `shipment-updated` | Shipping Service | Notification Service | Delivery updates |

### Event Flow Diagram

```
User Registration → user-registered → Welcome Email
                                   
Order Created → order-created → Payment Processing
                             → Order Confirmation
                             
Payment Success → payment-processed → Order Status Update
                                   → Shipment Creation
                                   → Payment Receipt
                                   
Shipment Created → shipment-created → Tracking Info Email
```

---

##  Resilience Patterns

### Circuit Breaker Configuration

Each service implements circuit breakers for external calls:

```yaml
# Example: Cart Service Circuit Breaker for Product Service
resilience4j:
  circuitbreaker:
    instances:
      productService:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        sliding-window-size: 10
```

### Retry Policies

- **Cart Service**: 3 retries for product service calls
- **Order Service**: 3 retries for inventory updates
- **Payment Service**: 2 retries for payment gateway
- **Shipping Service**: 2 retries for address fetching

### Rate Limiting

| Service | Limit |
|---------|-------|
| User Service (Registration) | 100 req/min |
| Product Service (Listing) | 200 req/min |
| Cart Service | 150 req/min |
| Order Service (Creation) | 100 req/min |
| Payment Service | 80 req/min |
| Notification Service | 200 req/min |

### Bulkhead Pattern

- **Product Service**: Max 10 concurrent inventory updates
- **Cart Service**: Max 15 concurrent checkouts
- **Order Service**: Max 20 concurrent order processing
- **Payment Service**: Max 10 concurrent payments
- **Notification Service**: Max 20 concurrent notifications

---

##  Domain-Driven Design

### Bounded Contexts

1. **Identity & Access** (User Service)
   - User, Address entities
   - Authentication and authorization

2. **Catalog & Inventory** (Product Service)
   - Product, Category, InventoryTransaction entities
   - Stock management

3. **Shopping** (Cart Service)
   - Cart, CartItem entities
   - Checkout orchestration

4. **Order Management** (Order Service)
   - Order, OrderItem, OrderEvent entities
   - Order lifecycle management

5. **Payment** (Payment Service)
   - Payment, Refund entities
   - Payment gateway integration

6. **Logistics** (Shipping Service)
   - Shipment, DeliveryEvent entities
   - Carrier integration

7. **Communication** (Notification Service)
   - Notification, Template entities
   - Multi-channel messaging

Each context owns its data and exposes only necessary operations through well-defined APIs.

---

##  Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

##  License

This project is licensed under the MIT License.

---

##  Authors

Wassim Lagnaoui

---

## 📞 Support

For questions or issues, please open an issue on GitHub or contact me at my email: 

---

##  Roadmap

- [ ] Add GraphQL API support
- [ ] Implement distributed tracing (Zipkin/Jaeger)
- [ ] Add monitoring dashboards (Grafana/Prometheus)
- [ ] Implement CQRS pattern for reporting
- [ ] Add Redis caching layer
- [ ] Implement OAuth2/JWT authentication
- [ ] Add API versioning
- [ ] Container orchestration (Kubernetes)
- [ ] CI/CD pipeline setup
- [ ] Performance testing suite

---

**Peace Out**

