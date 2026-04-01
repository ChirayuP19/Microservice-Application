# 🛒 Distributed Commerce Backend with Event Streaming

> A production-ready, event-driven e-commerce backend built with Spring Boot microservices, Kafka, Keycloak, and Docker.

---

## 📌 What is this?

CloudCart is a fully distributed e-commerce backend where users can **register, browse products, manage a cart, place orders, and receive email notifications** — all powered by independent microservices communicating via REST and Kafka events.

---

## 🏗️ Architecture Overview

```
Client
  └── API Gateway (8080)
        ├── User Service (8082)       → MongoDB + Keycloak
        ├── Product Service (8081)    → PostgreSQL + Elasticsearch
        ├── Order Service (8083)      → PostgreSQL + Kafka Producer
        └── Notification Service (8084) → MongoDB + Kafka Consumer + Email
```

All services register with **Eureka** and get config from **Config Server**.

---

## 🧩 Services

| Service | Port | Description |
|---|---|---|
| API Gateway | 8080 | Single entry point, rate limiting, circuit breaker |
| User Service | 8082 | Register/manage users via Keycloak |
| Product Service | 8081 | CRUD products, bulk upload via Excel, Elasticsearch search |
| Order Service | 8083 | Cart management + place orders + Kafka producer |
| Notification Service | 8084 | Kafka consumer + sends order confirmation emails |
| Config Server | 8888 | Centralized configuration for all services |
| Eureka | 8761 | Service discovery |

---

## 🔧 Tech Stack

- **Java 17** + **Spring Boot 3.4.3**
- **Spring Cloud Gateway** — routing, rate limiting, circuit breaker
- **Spring Cloud Config** — centralized config
- **Eureka** — service discovery
- **Keycloak 24** — authentication & user management
- **Kafka** — async order event streaming
- **PostgreSQL** — product & order data
- **MongoDB** — user & notification data
- **Redis** — rate limiting in gateway
- **Elasticsearch** — product full-text search
- **Zipkin** — distributed tracing
- **MailDev** — local email testing
- **Docker + Docker Compose** — containerized deployment

---

## 🚀 Quick Start (Run with Docker)

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed
- At least **8GB RAM** available for Docker
- If you are using in Windows and Linux please update latest version to **v1** other wise you go an error into docker container

### 1. Clone or download

```bash
git clone https://github.com/chirayupatel19/Microservice-Application.git
cd Microservice-Application
```

### 2. Create your `.env` file

Copy the example and fill in your values:

```bash
cp example.env .env
```

Your `.env` should look like:

```env
# MongoDB - User Service
MONGO_URI=mongodb://chirayu:chirayu@mongodb:27017/userdb?authSource=admin
MONGO_DB=userdb

# PostgreSQL - Product Service
DB_URL=jdbc:postgresql://postgres:5432/product
DB_USER=chirayu
DB_PASSWORD=chirayu

# PostgreSQL - Order Service
DB_URL_ORDER=jdbc:postgresql://postgres:5432/order
DB_USER_ORDER=chirayu
DB_PASSWORD_ORDER=chirayu
```

---
 
## 📦 Docker Hub Images
 
All service images are publicly available. No build needed!
 
| Service | Docker Hub Image |
|---|---|
| Config Server | `chirayupatel19/config-server:latest` |
| Eureka | `chirayupatel19/eureka:latest` |
| Gateway | `chirayupatel19/gateway-service:latest` |
| User Service | `chirayupatel19/user-service:latest` |
| Product Service | `chirayupatel19/product-service:latest` |
| Order Service | `chirayupatel19/order-service:latest` |
| Notification Service | `chirayupatel19/notification-service:latest` |
 
Pull all images manually if needed:
```bash
docker pull chirayupatel19/config-server:latest
docker pull chirayupatel19/eureka:latest
docker pull chirayupatel19/gateway-service:latest
docker pull chirayupatel19/user-service:latest
docker pull chirayupatel19/product-service:latest
docker pull chirayupatel19/order-service:latest
docker pull chirayupatel19/notification-service:latest
```
 
> 🔗 Docker Hub Profile: [hub.docker.com/u/chirayupatel19](https://hub.docker.com/u/chirayupatel19)
 
---
### 3. Start everything

```bash
docker compose up -d
```

> ⚠️ First run takes 3-5 minutes. Services start in order — be patient!

### 4. Verify all services are running

```bash
docker compose ps
```

All containers should show `running` or `healthy`.

---

## 🌐 Service URLs

| Service | URL |
|---|---|
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Keycloak Admin | http://localhost:8443 |
| MailDev (view emails) | http://localhost:1080 |
| PgAdmin | http://localhost:5050 |
| Mongo Express | http://localhost:7070 |
| Zipkin Tracing | http://localhost:9411 |
| RabbitMQ Dashboard | http://localhost:15672 |

---

## 🔐 Keycloak Setup (Required before using the API)

1. Open **http://localhost:8443**
2. Login with `admin` / `admin`
3. Create a new **Realm** called `ecom-app`
4. Inside that realm, create a **Client** called `admin-cli`
5. Create a test user inside the realm

> The user service uses Keycloak to register and manage users.

---

## 📖 API Reference

All requests go through the **API Gateway at port 8080**.

### 👤 User Service — `/api/v1/users`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/users` | Register a new user |
| `GET` | `/api/v1/users` | Get all users (paginated) |
| `GET` | `/api/v1/users/{id}` | Get user by ID |
| `PUT` | `/api/v1/users/{id}` | Update user |

**Create User Example:**
```json
POST /api/v1/users
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "secret123",
  "role": "USER",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "zipCode": "10001"
  }
}
```

---

### 📦 Product Service — `/api/v1/products`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/products` | Create a product |
| `GET` | `/api/v1/products` | Get all products (paginated) |
| `GET` | `/api/v1/products/{id}` | Get product by ID |
| `PUT` | `/api/v1/products/{id}` | Update product |
| `DELETE` | `/api/v1/products/{id}` | Delete product |
| `GET` | `/api/v1/products/search?keyword=shoes` | Search products (Elasticsearch) |
| `POST` | `/api/v1/products/bulk-upload` | Bulk upload via Excel file |

**Create Product Example:**
```json
POST /api/v1/products
{
  "name": "Running Shoes",
  "description": "Lightweight running shoes",
  "price": 89.99,
  "stockQuantity": 100,
  "category": "Footwear"
}
```

---

### 🛒 Cart Service — `/api/v1/cart`

> Pass `X-User-Id` header with your user ID for all cart requests.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/cart` | Add item to cart |
| `GET` | `/api/v1/cart` | View cart |
| `DELETE` | `/api/v1/cart/items/{productId}` | Remove item from cart |

**Add to Cart Example:**
```bash
POST /api/v1/cart
X-User-Id: your-user-id

{
  "productId": 1,
  "quantity": 2
}
```

---

### 📋 Order Service — `/api/v1/orders`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/orders` | Place order from cart |

**Place Order Example:**
```bash
POST /api/v1/orders
X-User-Id: your-user-id
```

> When order is placed, a **Kafka event** is fired → Notification Service picks it up → sends confirmation **email** to the user.

---

### 🔔 Notification Service — `/api/v1/notifications`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/notifications` | Get all notifications |

---

## 📧 How Email Notifications Work

```
User places order
      ↓
Order Service → publishes event to Kafka topic
      ↓
Notification Service → consumes event
      ↓
Sends email via MailDev (local) or SMTP (production)
      ↓
View email at http://localhost:1080
```

---

## 📊 Swagger API Docs

Each service has its own Swagger UI:

| Service | Swagger URL |
|---|---|
| User Service | http://localhost:8082/swagger-ui.html |
| Product Service | http://localhost:8081/swagger-ui.html |
| Order Service | http://localhost:8083/swagger-ui.html |
| Notification Service | http://localhost:8084/swagger-ui.html |

---

## 🐳 Docker Hub Images

All images are publicly available — no build required!

| Image | Link |
|---|---|
| config-server | `chirayupatel19/config-server:latest` |
| eureka | `chirayupatel19/eureka:latest` |
| gateway-service | `chirayupatel19/gateway-service:latest` |
| user-service | `chirayupatel19/user-service:latest` |
| product-service | `chirayupatel19/product-service:latest` |
| order-service | `chirayupatel19/order-service:latest` |
| notification-service | `chirayupatel19/notification-service:latest` |

---

## 🛠️ Default Credentials

| Service | Username | Password |
|---|---|---|
| PostgreSQL | chirayu | chirayu |
| MongoDB | chirayu | chirayu |
| RabbitMQ | admin | admin123 |
| Keycloak | admin | admin |
| PgAdmin | pgadmin4@pgadmin.org | admin |
| Mongo Express | chirayu | chirayu |

---

## 🗂️ Project Structure

```
Microservice-Application/
├── configserver/       # Spring Cloud Config Server
├── eureka/             # Service Discovery
├── gateway/            # API Gateway
├── user/               # User Service
├── product/            # Product Service
├── order/              # Order + Cart Service
├── notification/       # Notification Service
├── docker-compose.yml  # Full stack setup
└── example.env         # Environment variables template
```

---

## ⚠️ Common Issues

**Services not starting?**
Wait 3-5 minutes. Services depend on each other and start in order.

**Keycloak errors in user-service?**
Make sure you created the `ecom-app` realm in Keycloak before starting.

**Emails not received?**
Check MailDev at http://localhost:1080 — all emails are captured there locally.

**Out of memory errors?**
Increase Docker Desktop memory to at least 8GB in Settings → Resources.

---

## 👨‍💻 Author

**Chirayu Patel**
- Docker Hub: [chirayupatel19](https://hub.docker.com/u/chirayupatel19)
- GitHub: [chirayupatel19](https://github.com/ChirayuP19)
