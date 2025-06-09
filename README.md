# 💸 Personal Finance Tracker API

A secure and user-friendly backend API for tracking personal financial transactions. Built using **Java, Spring Boot, and OAuth2**, this service allows users to add, view, filter, and export their financial transactions.

---

## 🚀 Features

- ✅ **User Authentication** via Google OAuth2
- 💰 **Add, view, delete transactions**
- 📊 **Transaction summary** (Income & Expense totals)
- 🧾 **Filter transactions** by date range
- 📤 **Export transactions to CSV**
- 🔒 Secured access — each user sees only their own data
- 📄 **Swagger/OpenAPI** documentation for easy API testing

---

## 🛠️ Tech Stack

- Java 17+
- Spring Boot (Web, Security, OAuth2, Data JPA)
- PostgreSQL / H2 (dev)
- Swagger (Springdoc OpenAPI)
- Maven

---

## 📌 API Endpoints Overview

### 🔐 Auth

| Method | Endpoint             | Description                        |
|--------|----------------------|------------------------------------|
| GET    | `/api/auth/success`  | Returns logged-in user's basic info |
| GET    | `/api/auth/api/me`   | Returns full user entity (secured) |

---

### 🔁 Transactions

| Method | Endpoint                     | Description                                  |
|--------|------------------------------|----------------------------------------------|
| POST   | `/api/transactions`          | Add a new transaction                        |
| GET    | `/api/transactions`          | Get all transactions for logged-in user      |
| DELETE | `/api/transactions/{txId}`   | Delete transaction by ID                     |
| GET    | `/api/transactions/summary`  | Get total income & expenses                  |
| GET    | `/api/transactions/filter`   | Filter by date range (`from`, `to`)          |
| GET    | `/api/transactions/export`   | Export transactions as CSV                   |

---

## 🔐 Authentication Setup (Google OAuth2)

1. Create a Google Client ID and Secret from [Google Developer Console](https://console.developers.google.com).
2. Configure in your `application.yml` or `application.properties`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_CLIENT_ID
            client-secret: YOUR_CLIENT_SECRET
