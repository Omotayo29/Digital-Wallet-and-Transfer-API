# Digital Wallet and Transfer API
## About the project

Digital Wallet and Transfer API is a Spring Boot backend for managing users, wallets, funding operations, and wallet-to-wallet transfers.

When a user is created, the application creates one wallet for that user with a zero balance. Wallets currently use `NGN` by default. A wallet can be funded, and users can transfer money to another user's wallet.

The project is designed around the main rules that a wallet system needs:

- Every user has one wallet.
- User email addresses must be unique.
- Funding increases a wallet balance and records a `FUNDING` transaction.
- Transfers require a positive amount and sufficient funds.
- A user cannot transfer money to themselves.
- Sender and receiver wallets must use the same currency.
- A transfer debits the sender, credits the receiver, and records the transaction together.
- Transfer requests use an `Idempotency-Key` so a retry does not create a second transfer.
- Transaction history is returned with Spring's pagination support.

## Application structure

The code is organized into layers:

```text
controller   HTTP endpoints and request handling
service      wallet, user, funding, and transfer business logic
repository   Spring Data JPA database access
model        User, Wallet, and Transaction entities
dto          request and response objects
mappers      entity-to-DTO and DTO-to-entity conversion
exception    custom exceptions and the global error handler
config       Swagger/OpenAPI configuration
```

The transfer operation uses a separate transactional service. This keeps the sender debit, receiver credit, and transaction insert in one database transaction. The outer transfer service checks idempotency and handles a duplicate-key race when two requests use the same key.

## Main domain objects

### User

A user has:

- `id`
- `fullName`
- unique `email`
- `createdAt`

### Wallet

A wallet has:

- `id`
- one linked user
- `balance`
- `currency`
- `createdAt`
- `version` for optimistic locking

### Transaction

A transaction records:

- unique `reference`
- optional unique `idempotencyKey`
- transaction `type`: `FUNDING` or `TRANSFER`
- sender wallet, when applicable
- receiver wallet
- amount
- status: `SUCCESS` or `FAILED`
- `createdAt`

Funding transactions have no sender wallet because the money comes from outside the application.

## API capabilities

### User operations

```text
POST /user/createUser
GET  /user/getUser/{id}
```

### Wallet operations

```text
GET  /wallet/getWallet/{walletId}
POST /wallet/fundWallet/{walletId}
GET  /wallet/transactionHistory/{walletId}?page=0&size=10
```

### Transfer operation

```text
POST /transfer/createTransfer
```

The transfer endpoint requires the `Idempotency-Key` header:

```text
Idempotency-Key: transfer-adaeze-tunde-001
```

Example request:

```json
{
  "senderUserId": "sender-user-uuid",
  "receiverUserId": "receiver-user-uuid",
  "amount": 1000.00
}
```

The same idempotency key should be reused when retrying the same request. A new transfer should use a new key.

## Validation and errors

Request DTOs validate input before it reaches the services:

- Names cannot be blank.
- Emails must be valid.
- Amounts must be present and positive.

The API uses custom exceptions for common failures:

| Exception | HTTP status | Meaning |
|---|---:|---|
| `ResourceNotFoundException` | 404 | User or wallet does not exist |
| `DuplicateEmailException` | 409 | Email is already registered |
| `InsufficientBalanceException` | 400 | Sender has insufficient funds |
| `InvalidTransferException` | 400 | Invalid amount, self-transfer, or currency mismatch |

Errors are handled by the global exception handler and returned in a consistent response format.

## Technology

- Java 25
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Jakarta Bean Validation
- Lombok
- Mockito and JUnit
- Springdoc OpenAPI

This project uses PostgreSQL for application data. It does not use H2 or Flyway; the schema is currently managed during development with Hibernate.

