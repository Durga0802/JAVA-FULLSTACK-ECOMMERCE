# Full Stack E-Commerce Admin Panel (Spring Boot)

A full-stack e-commerce application built using Spring Boot, MySQL, JWT authentication, and a Vanilla JavaScript frontend. This project includes an admin dashboard, product management system, order handling, revenue analytics, authentication, pagination, Swagger API documentation, and hybrid image handling.

## Live Demo

* Deployed Application: [https://ecommerce-fullstack-dz04.onrender.com](https://ecommerce-fullstack-dz04.onrender.com)
* GitHub Repository: [https://github.com/prajapatpiyush/ecommerce-fullstack](https://github.com/prajapatpiyush/ecommerce-fullstack)

---

# Project Overview

This project was built to learn Spring Boot properly through a real-world full-stack application instead of basic CRUD tutorials.

The application includes:

* Admin login with JWT authentication
* Product management
* Category management
* Order management
* Revenue dashboard analytics
* User-side product browsing and ordering
* Swagger API documentation
* Hybrid image management system

---

# Tech Stack

## Backend

* Java 17
* Spring Boot
* Spring Data JPA
* Spring Security
* JWT Authentication
* Maven
* Swagger / OpenAPI

## Frontend

* HTML5
* CSS3
* JavaScript 
* Fetch API

## Database

* MySQL (Railway)

## Deployment

* Render (Backend + Frontend Hosting)
* Railway (Database Hosting)

---

# Features

## Authentication & Security

* JWT-based authentication
* Role-based authorization
* Admin and User roles
* Password encryption using BCrypt
* Protected routes and APIs

## Product Management

* Create products
* Delete products
* View all products
* Product image support
* Category assignment
* Pagination and sorting support

## Order Management

* Place orders
* View orders
* Calculate total order price
* Track ordered products
* Revenue analytics dashboard

## Frontend Features

* Admin login page
* Product listing page
* Product creation page
* Order viewing page
* Dashboard analytics page
* JWT token storage using localStorage
* Route protection for admin pages

## API Features

* RESTful API architecture
* DTO pattern implementation
* Validation using annotations
* Global exception handling
* Swagger API documentation
* Pagination and sorting

---

# Project Architecture

```
Client
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

## Backend Folder Structure

```text
src/main/java
│
├── controller
├── service
├── repository
├── entity
├── dto
├── security
├── config
├── exception
└── util
```

## Frontend Folder Structure

```text
frontend
│
├── index.html
├── login.html
├── products.html
├── create-product.html
├── orders.html
├── dashboard.html
├── style.css
└── app.js
```

---

# Database Design

## Tables

* users
* categories
* product
* orders
* order_items

## Relationships

```text
User
  │
  └── OneToMany → Orders

Order
  │
  └── OneToMany → OrderItems

OrderItem
  │
  └── ManyToOne → Product

Product
  │
  └── ManyToOne → Category
```

## Important JPA Annotations Used

* @Entity
* @Id
* @GeneratedValue
* @OneToMany
* @ManyToOne
* @JoinColumn
* @JosnIgnore

---

# API Endpoints

## Authentication

```http
POST /auth/login
```

## Products

```http
GET /api/products
GET /api/products/{id}
POST /api/products
PUT /api/products/{id}
DELETE /api/products/{id}
```

## Categories

```http
GET /api/categories
POST /api/categories
DELETE /api/categories/{id}
```

## Orders

```http
GET /api/orders
POST /api/orders
```

## Pagination Example

```http
GET /api/products?page=0&size=5&sortBy=price
```

---

# JWT Authentication Flow

```text
POST /auth/login
        ↓
Verify email and password
        ↓
Generate JWT token
        ↓
Return token to client
        ↓
Client stores token in localStorage
        ↓
Token sent in Authorization header
```

Example:

```http
Authorization: Bearer your_jwt_token
```

---

# Hybrid Image Management System

Initially, the project used only file uploads with MultipartFile.

This created a major issue after deployment on Render because the uploads folder gets deleted whenever the server restarts or goes idle.

To solve this problem, a hybrid image management system was implemented.

## Current Supported Methods

1. Upload image file
2. Provide external image URL

Recommended approach:

* Use image URLs instead of file uploads in production

Example:

```text
https://images.unsplash.com/example-image
```

## Smart Image Rendering Logic

The frontend checks:

* If imageUrl starts with http → render directly
* If imageUrl is a file name → fetch from backend image endpoint

---

# Major Production Issues Solved

## 1. Render Ephemeral Storage Issue

Problem:

* Uploaded images disappeared after Render server restart

Solution:

* Added support for external image URLs
* Stored image URLs directly in database

## 2. MultipartFile API Crash

Problem:

* Backend expected MultipartFile only
* API returned 500 Internal Server Error when image URL was sent

Solution:

* Made MultipartFile optional
* Added logic for both uploaded files and image URLs

## 3. Database URL Length Issue

Problem:

* image_url column used VARCHAR(255)
* Long image URLs caused data truncation errors

Solution:

```sql
ALTER TABLE product MODIFY image_url TEXT;
```

---

# Validation & Exception Handling

## Validation Annotations Used

* @NotBlank
* @NotNull
* @Min
* @Valid

Example:

```java
@Min(1)
private double price;
```

## Exception Handling

Implemented using:

* @ControllerAdvice
* @ExceptionHandler
* ResourceNotFoundException
* GlobalExceptionHandler

Example Error Response:

```json
{
  "message": "Category not found",
  "status": 404,
  "timestamp": "2026-03-31"
}
```

---

# Swagger Documentation

Swagger UI available at:

```text
http://localhost:8080/swagger-ui/index.html
```

For deployed environment:

```text
https://ecommerce-fullstack-dz04.onrender.com/swagger-ui/index.html
```

---

# Admin Credentials

```text
Email: piyush@test.com
Password: admin123
```

---

# Local Setup

## Clone Repository

```bash
git clone https://github.com/prajapatpiyush/ecommerce-fullstack.git
```

## Configure Database

Update application.properties:

```properties
spring.datasource.url=your_database_url
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Run Backend

```bash
mvn spring-boot:run
```

## Open Application

```text
http://localhost:8080
```

---

# What This Project Demonstrates

This project demonstrates:

* Spring Boot backend development
* Secure API development using JWT
* Database relationship modeling
* REST API design
* DTO architecture
* Exception handling
* Pagination and sorting
* Frontend-backend integration
* Production deployment debugging
* Image handling strategies for cloud deployment

---

# Future Improvements

* Docker support
* Unit testing
* Logging with SLF4J
* Redis caching
* Payment gateway integration
* Email notifications
* Wishlist and cart persistence
* Product search and filtering
* Postman collection
* Database schema diagram

---

# Author

Piyush Prajapati

* GitHub: [https://github.com/prajapatpiyush](https://github.com/prajapatpiyush)
* Project Repository: [https://github.com/prajapatpiyush/ecommerce-fullstack](https://github.com/prajapatpiyush/ecommerce-fullstack)
* Project Url:  [https://ecommerce-fullstack-dz04.onrender.com](https://ecommerce-fullstack-dz04.onrender.com)


# Screenshot's

<!-- Screenshot 1 -->
<img width="100%" alt="Screenshot 2026-04-20 162504" src="https://github.com/user-attachments/assets/1186a54e-6c7e-44fd-ace6-4f232a776854" />

<br><br>

<!-- Screenshot 2 -->
<img width="100%" alt="Admin-Login" src="https://github.com/user-attachments/assets/eb41148e-b49d-486d-a0ac-b07e23075abe" />

<br><br>

<!-- Screenshot 3 -->
<img width="100%" alt="Admin-Dashboard" src="https://github.com/user-attachments/assets/60ff4111-4e33-430e-8eaf-b750905703b3" />

<br><br>

<!-- Screenshot 4 -->
<img width="100%" alt="Manage-Products" src="https://github.com/user-attachments/assets/a2de01a4-deff-431e-9563-e661614e0896" />

<br><br>

<!-- Screenshot 5 -->
<img width="100%" alt="Manage-Orders" src="https://github.com/user-attachments/assets/62ba1b1d-ecc7-49cc-8f2e-4b16c0923e70" />
