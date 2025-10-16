# BankingSystem Backend API Documentation

Base URL: http://localhost:8080/api/v1

## Authentication (Demo Only)
Passwords stored in plain text (NOT for production). No tokens. Frontend stores user in localStorage.

| Method | Path              | Description             | Request Body Example |
|--------|-------------------|-------------------------|----------------------|
| POST   | /auth/login       | Login with email/pass   | {"email":"a@b.com","password":"Abc123"} |
| POST   | /auth/logout      | No-op logout            | N/A                  |

Response (200 OK login):
```
{
  "id": 1,
  "firstName": "Alice",
  "lastName": "Smith",
  "email": "alice@example.com",
  "phone": "1234567890",
  "dob": "1990-05-10",
  "createdAt": "2025-10-01T10:29:15.193130Z"
}
```

## Customers
| Method | Path                | Description          |
|--------|---------------------|----------------------|
| POST   | /customers          | Create customer      |
| GET    | /customers/{id}     | Get customer by id   |

Create Customer Request:
```
{
  "firstName":"Alice",
  "lastName":"Smith",
  "email":"alice@example.com",
  "phone":"1234567890",
  "dob":"1990-05-10",
  "password":"Abc123"
}
```
Validation: email unique, phone 10 digits, age >= 18, password >= 6 chars with letter+digit.

## Accounts
| Method | Path                                | Description                          |
|--------|-------------------------------------|--------------------------------------|
| POST   | /accounts                           | Create account                       |
| GET    | /accounts/{id}                      | Get account                          |
| POST   | /accounts/{id}/deposit              | Deposit money                        |
| POST   | /accounts/{id}/withdraw             | Withdraw money                       |
| POST   | /accounts/transfer                  | Transfer between accounts            |
| GET    | /accounts/{id}/transactions         | List transactions for account        |
| GET    | /accounts/by-customer/{customerId}  | List accounts for a customer         |
| DELETE | /accounts/{id}                      | Close account (balance must be zero) |

Create Account Request:
```
{
  "customerId": 1,
  "accountType": "SAVINGS",  // or CURRENT
  "openingBalance": 500.00,
  "interestRate": 0.015
}
```

Deposit / Withdraw Request:
```
{
  "amount": 100.00,
  "note": "ATM"
}
```

Transfer Request:
```
{
  "fromAccountId": 10,
  "toAccountId": 12,
  "amount": 50.00,
  "note": "Test transfer"
}
```
Response (Account):
```
{
  "id": 10,
  "accountNumber": "123456789012",
  "customerId": 1,
  "accountType": "SAVINGS",
  "balance": 500.00,
  "openedAt": "2025-10-01T10:35:12.123456Z",
  "interestRate": 0.015,
  "overdraftLimit": null,
  "status": "ACTIVE"
}
```

Transaction Response:
```
{
  "id": 55,
  "txnType": "DEPOSIT", // WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, OPENING_DEPOSIT
  "amount": 150.00,
  "txnDate": "2025-10-01T10:40:15.456Z",
  "note": "Cash"
}
```

## Error Formats
Validation error (MethodArgumentNotValidException):
```
{
  "timestamp": "...",
  "status": 400,
  "error": "validation_error",
  "message": "Validation failed",
  "fieldErrors": [ { "field": "email", "message": "must not be blank" } ]
}
```
Bad request (IllegalArgumentException):
```
{
  "timestamp": "...",
  "status": 400,
  "error": "bad_request",
  "message": "Email already registered"
}
```
Conflict (IllegalStateException):
```
{
  "error": "conflict",
  "message": "Insufficient funds"
}
```

## Swagger / OpenAPI
- UI: http://localhost:8080/swagger-ui/index.html
- JSON: http://localhost:8080/v3/api-docs

## Notes & Limitations
- No real authentication/authorization beyond plain login.
- Backend does NOT enforce account ownership (frontend hides others; server would need security layer for production).
- Passwords are NOT hashed (demo requirement).
- No pagination on transactions.

## Suggested Improvements (Future)
1. Add password hashing (BCrypt) & JWT tokens.
2. Add server-side ownership checks on every account/transaction endpoint.
3. Introduce pagination & sorting.
4. Add audit logging.
5. Add optimistic locking (version column) to prevent lost updates.


