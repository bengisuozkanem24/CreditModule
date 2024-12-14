# Loan Management API

This project provides a RESTful API for managing loans and installments for a credit module application. The application includes features such as creating loans, managing installments, and paying off loans. It uses Spring Boot with PostgreSQL as the database.

---

## Prerequisites

1. **Java**: Version 17 or higher.
2. **Maven**: Build tool to manage dependencies.
3. **PostgreSQL**: Database server.

---

## Setup Instructions

### 1. Configure the Database

Update the `application.properties` file in `src/main/resources/` with your PostgreSQL connection details:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/loan_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start at `http://localhost:8080`.

---

## Endpoints

### Authentication
All endpoints require Basic Authentication. Use the following default credentials:
- **Username**: `admin`
- **Password**: `admin123`

### Customer Endpoints
- **Create Customer**:
  ```http
  POST /api/loans/createCustomer
  ```
  **Request Body**:
  ```json
  {
      "name": "John",
      "surname": "Doe",
      "creditLimit": 10000,
      "usedCreditLimit": 0
  }
  ```

### Loan Endpoints
- **Create Loan**:
  ```http
  POST /api/loans/create
  ```
  **Request Body**:
  ```json
  {
      "customerId": 1,
      "amount": 5000,
      "interestRate": 0.2,
      "numberOfInstallments": 12
  }
  ```

- **List Loans by Customer**:
  ```http
  GET /api/loans/list/{customerId}
  ```

### Installment Endpoints
- **List Loan Installments**:
  ```http
  GET /api/loans/installments/{loanId}
  ```

- **Pay Installment**:
  ```http
  POST /api/loans/pay
  ```
  **Request Body**:
  ```json
  {
      "loanId": 1,
      "amount": 5000
  }
  ```

---

## Running Tests

To run tests:

```bash
mvn test
```

---

## Technologies Used

- **Java**: Backend development.
- **Spring Boot**: Framework for creating RESTful APIs.
- **PostgreSQL**: Database management.
- **JUnit**: Unit testing framework.
- **Mockito**: Mocking framework for testing.
- **Lombok**: Reduces boilerplate code.