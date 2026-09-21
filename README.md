# BT09 - GraphQL with Spring Boot

## 1. Introduction

This project is a Web Programming assignment implemented with:

- Spring Boot
- Spring for GraphQL
- Spring Data JPA
- Thymeleaf
- AJAX / Fetch API
- Microsoft SQL Server

The application manages `Category` and `Product` data through GraphQL and renders the results on Thymeleaf HTML pages using AJAX.

---

## 2. Technologies

- Java 21
- Spring Boot 4.1.1
- Spring for GraphQL
- Spring Data JPA
- Thymeleaf
- JavaScript Fetch API
- Microsoft SQL Server
- Maven Wrapper
- Lombok

---

## 3. Database Model

### Category

Fields:

- `id`
- `name`
- `images`

### User

Fields:

- `id`
- `fullname`
- `email`
- `password`
- `phone`

### Product

Fields:

- `id`
- `title`
- `quantity`
- `description`
- `price`
- `user_id`
- `category_id`

### Relationships

- User N:N Category
- Category 1:N Product
- User 1:N Product
- Product N:1 Category
- Product N:1 User

The many-to-many relationship between User and Category uses the join table:

```text
user_category
```

---

## 4. Main Features

### Home

- Display all Products ordered by price from low to high.
- Filter Products by Category.
- Product and Category data are loaded through AJAX and GraphQL.

### Product Management

- View Products
- Create Product
- Update Product
- Delete Product

All Product operations use GraphQL.

### Category Management

- View Categories
- Create Category
- Update Category
- Delete Category

All Category operations use GraphQL.

A Category cannot be deleted while Products are still associated with it.

---

## 5. GraphQL API

GraphQL endpoint:

```text
POST http://localhost:8088/graphql
```

### Queries

```graphql
categories
category(id: ID!)

products
product(id: ID!)

productsByPriceAsc
productsByCategory(categoryId: ID!)
```

### Mutations

```graphql
createCategory
updateCategory
deleteCategory

createProduct
updateProduct
deleteProduct
```

The project uses a custom GraphQL `BigDecimal` scalar for Product prices.

---

## 6. Requirements

Before running the project, install:

- Java 21
- Microsoft SQL Server
- Git

Maven does not need to be installed separately because the project includes Maven Wrapper.

Check Java version:

```powershell
java -version
```

Expected major version:

```text
21
```

---

## 7. Clone the Project

```powershell
git clone https://github.com/pminhquan/BT09_GraphQL.git
cd BT09_GraphQL
```

---

## 8. Create SQL Server Database

Connect to SQL Server using SQL Server Management Studio, Azure Data Studio, `sqlcmd`, or another SQL client.

Create the database:

```sql
CREATE DATABASE BT09_GraphQL;
GO
```

The application connects to:

```text
localhost:1433
```

Database name:

```text
BT09_GraphQL
```

---

## 9. Database Configuration

The SQL Server password is not stored directly in source code.

Current datasource configuration:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=BT09_GraphQL;encrypt=true;trustServerCertificate=true
spring.datasource.username=${DB_USERNAME:sa}
spring.datasource.password=${DB_PASSWORD}
```

Before starting the application, set the SQL Server credentials.

### Windows PowerShell

```powershell
$env:DB_USERNAME="sa"
$env:DB_PASSWORD="YOUR_SQL_SERVER_PASSWORD"
```

Example:

```powershell
$env:DB_USERNAME="sa"
$env:DB_PASSWORD="your_password_here"
```

Do not commit a real database password to GitHub.

These environment variables are valid for the current PowerShell session.

---

## 10. Run the Application

From the project directory:

```powershell
.\mvnw.cmd spring-boot:run
```

The application runs by default at:

```text
http://localhost:8088
```

---

## 11. Database Tables

The project uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

After the application successfully connects to the database, Hibernate automatically creates or updates the required tables.

Expected tables:

```text
categories
products
users
user_category
```

---

## 12. Create a User for Product Testing

A Product requires an existing `user_id`.

User CRUD is not part of this assignment, so before testing Product creation, make sure at least one User exists in the database.

First, start the application once so Hibernate creates the tables.

Then execute:

```sql
USE BT09_GraphQL;
GO

INSERT INTO users (fullname, email, password, phone)
VALUES (
    'Test User',
    'test@example.com',
    '123456',
    '0123456789'
);
GO
```

Check the available Users:

```sql
SELECT * FROM users;
GO
```

Use the returned `id` as `userId` when creating a Product.

The password above is only sample local test data.

---

## 13. Application Pages

### Home

```text
http://localhost:8088/
```

or:

```text
http://localhost:8088/home
```

The Home page:

- loads Categories through GraphQL AJAX
- displays Products ordered by ascending price
- filters Products by Category

### Product Management

```text
http://localhost:8088/products
```

Functions:

- Create Product
- Read Product list
- Update Product
- Delete Product

### Category Management

```text
http://localhost:8088/categories
```

Functions:

- Create Category
- Read Category list
- Update Category
- Delete Category

---

## 14. Testing Flow

This project does not implement authentication, Admin/Customer roles, or User CRUD because they are not required by this assignment.

The `User` entity is used for database relationships with `Category` and `Product`.

Recommended testing flow:

1. Create the `BT09_GraphQL` database.
2. Set the SQL Server environment variables.
3. Start the application once so Hibernate creates the tables.
4. Insert one test User into the `users` table.
5. Open `/categories` and create a Category.
6. Open `/products` and create a Product using:
   - an existing `userId`
   - an existing `categoryId`
7. Open `/home`.
8. Verify Products are displayed by ascending price.
9. Select a Category and verify only Products of that Category are displayed.
10. Test Product create/update/delete.
11. Test Category create/update/delete.

A Category containing Products cannot be deleted until its Products are removed.

---

## 15. AJAX Flow

The Thymeleaf controllers only return HTML views.

Product and Category data are not loaded from Spring MVC Model attributes.

JavaScript sends AJAX requests directly to:

```text
POST /graphql
```

Request format:

```json
{
  "query": "...",
  "variables": {}
}
```

Main JavaScript files:

```text
src/main/resources/static/js/graphql.js
src/main/resources/static/js/home.js
src/main/resources/static/js/products.js
src/main/resources/static/js/categories.js
```

---

## 16. Example GraphQL Queries

### Get Categories

```graphql
query {
  categories {
    id
    name
    images
  }
}
```

### Get All Products

```graphql
query {
  products {
    id
    title
    quantity
    description
    price
    userId
    categoryId
    categoryName
    userFullname
  }
}
```

### Products Ordered by Price

```graphql
query {
  productsByPriceAsc {
    id
    title
    quantity
    price
    categoryName
    userFullname
  }
}
```

### Products by Category

```graphql
query {
  productsByCategory(categoryId: "1") {
    id
    title
    quantity
    price
    categoryName
  }
}
```

---

## 17. Example GraphQL Mutations

### Create Category

```graphql
mutation {
  createCategory(
    input: {
      name: "Electronics"
      images: "electronics.jpg"
    }
  ) {
    id
    name
    images
  }
}
```

### Update Category

```graphql
mutation {
  updateCategory(
    id: "1"
    input: {
      name: "Consumer Electronics"
      images: "consumer-electronics.jpg"
    }
  ) {
    id
    name
    images
  }
}
```

### Delete Category

```graphql
mutation {
  deleteCategory(id: "1")
}
```

A Category containing Products cannot be deleted until those Products are removed.

### Create Product

Make sure `userId` and `categoryId` already exist.

```graphql
mutation {
  createProduct(
    input: {
      title: "Mechanical Keyboard"
      quantity: 10
      description: "RGB mechanical keyboard"
      price: 99.95
      userId: "1"
      categoryId: "1"
    }
  ) {
    id
    title
    quantity
    price
    categoryName
    userFullname
  }
}
```

### Update Product

```graphql
mutation {
  updateProduct(
    id: "1"
    input: {
      title: "Mechanical Keyboard V2"
      quantity: 20
      description: "Updated mechanical keyboard"
      price: 119.99
      userId: "1"
      categoryId: "1"
    }
  ) {
    id
    title
    quantity
    price
  }
}
```

### Delete Product

```graphql
mutation {
  deleteProduct(id: "1")
}
```

---

## 18. Run Tests

Run all tests:

```powershell
.\mvnw.cmd test
```

The project contains tests for:

- Spring Boot application context
- Category Service
- Product Service
- Category GraphQL Controller
- Product GraphQL Controller
- Thymeleaf View Controller

---

## 19. Build Project

Build without running tests:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Generated JAR:

```text
target/bt09-graphql-0.0.1-SNAPSHOT.jar
```

---

## 20. Project Structure

```text
src
├── main
│   ├── java
│   │   └── vn.edu.hcmute.bt09graphql
│   │       ├── config
│   │       ├── controller
│   │       │   ├── graphql
│   │       │   └── view
│   │       ├── dto
│   │       ├── entity
│   │       ├── repository
│   │       └── service
│   │
│   └── resources
│       ├── graphql
│       │   └── schema.graphqls
│       ├── static
│       │   ├── css
│       │   └── js
│       ├── templates
│       │   ├── home.html
│       │   ├── products.html
│       │   └── categories.html
│       └── application.properties
│
└── test
    └── java
```

---

## 21. Quick Start

### Step 1 - Clone

```powershell
git clone https://github.com/pminhquan/BT09_GraphQL.git
cd BT09_GraphQL
```

### Step 2 - Create Database

```sql
CREATE DATABASE BT09_GraphQL;
GO
```

### Step 3 - Set SQL Server Credentials

```powershell
$env:DB_USERNAME="sa"
$env:DB_PASSWORD="YOUR_SQL_SERVER_PASSWORD"
```

### Step 4 - Run

```powershell
.\mvnw.cmd spring-boot:run
```

### Step 5 - Create Test User

After the application starts once and Hibernate creates the tables:

```sql
USE BT09_GraphQL;
GO

INSERT INTO users (fullname, email, password, phone)
VALUES ('Test User', 'test@example.com', '123456', '0123456789');
GO
```

### Step 6 - Open the Application

```text
Home:
http://localhost:8088/

Product Management:
http://localhost:8088/products

Category Management:
http://localhost:8088/categories
```

Recommended order for manual testing:

```text
1. Create User in database
2. Create Category
3. Create Product
4. Test Product CRUD
5. Test Category filtering on Home
6. Test ascending Product price order on Home
7. Delete Products
8. Delete Category
```

---

## 22. Notes

- Authentication is not implemented because it is outside the scope of this assignment.
- There are no Admin or Customer roles.
- User CRUD is not implemented because it is not required.
- `User` is used to satisfy the required entity relationships with Product and Category.
- Product CRUD requires a valid existing `userId`.
- Category CRUD requires no login.
- Product and Category operations are performed through GraphQL.
- The UI uses AJAX / Fetch API to call `/graphql`.
- SQL Server credentials are supplied using environment variables.
