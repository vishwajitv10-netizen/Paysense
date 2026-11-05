# PaySense SafeGuard

A full-stack **UPI safety and digital-wallet demo** inspired by the linked UPI SafeGuard and PaySense projects. The frontend is React + Vite; the backend is Java 21 / Spring Boot 3, Spring Security, JWT, Spring Data JPA, Flyway, and PostgreSQL. Redis provides shared cache infrastructure; Kafka receives post-commit transaction domain events.

The React frontend source is under `frontend/src/`, with its Vite entry file at `frontend/index.html`. The UI provides sign-in/sign-up and pages for wallet overview, payments, transaction history, payment requests, split bills, QR/UPI safety checks, mobile recharge, bill pay, risk assessment, fraud reports, scam education, guardian mode, community stats, challenges, safety assistant, profile, settings, cards, and administrator sections. Pages are hash-addressable (for example `/#Risk%20Assessment`). Pages that need unavailable providers/roles are marked as demo or restricted rather than claiming live functionality.

> **Sandbox only:** no bank/UPI network, payment gateway, trained ML model, guardian workflow, carrier/bill provider, or external AI is connected. New accounts receive a configurable simulated balance. Rules-based risk hints are not a fraud guarantee. Never use real financial data or expose the demo publicly. Production UPI services require licensed payment partners, operational controls, and security/compliance review.

## Run the complete stack

Requires Docker Compose.

```sh
docker compose up --build
```

Open <http://localhost:8088>, create an account, then explore the dashboard/pages. Services: React/Nginx frontend, Spring API, PostgreSQL, Redis, and single-node Kafka. Data is persisted in named Docker volumes. Reset all local data with `docker compose down -v`.

Set strong local deployment secrets before sharing a deployment:

```sh
export APP_JWT_SECRET="generate-a-long-random-secret-of-at-least-32-characters"
export DATABASE_PASSWORD="use-a-strong-unique-password"
docker compose up --build
```

Compose's fallback credentials are for local development only. Do not deploy with those defaults.

## Run without Docker

Requires Java 21, Maven 3.9+, Node.js 18+, PostgreSQL, Redis, and Kafka.

1. Create the `paysense` PostgreSQL database and configure `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD` as needed.
2. Start Redis and Kafka (Kafka broker address defaults to `localhost:9092`).
3. Run `npm run api` in one terminal.
4. Run `npm install && npm run dev` in another. Vite proxies `/api` to Spring Boot on `8080`.

Backend tests: `mvn -f backend/pom.xml test` (isolated H2 test profile). Frontend production build: `npm run build`.

## API

All payloads are JSON. Registration and login are public; the other `/api/**` endpoints require `Authorization: Bearer <JWT>`.

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/auth/register`, `/api/auth/login` | Create demo account / obtain JWT |
| `GET` | `/api/auth/me`, `/api/users/me` | Current account profile |
| `PUT` | `/api/users/me` | Update name and Indian mobile number |
| `GET` | `/api/dashboard` | Wallet balance and current-month totals |
| `GET` | `/api/transactions?q=...&page=0&size=20` | Search and paginate transaction history |
| `GET` | `/api/transactions/{id}` | Read a transaction owned by the caller |
| `POST` | `/api/transfers` | Simulated wallet transfer with idempotency support |
| `POST` | `/api/payments` | Simulated merchant payment |
| `POST` | `/api/transactions/{id}/refund` | Refund an eligible debit once |
| `GET`, `POST` | `/api/cards` | List/create masked virtual demo cards |
| `PATCH` | `/api/cards/{id}/freeze?frozen=true` | Freeze/unfreeze an owned demo card |
| `POST` | `/api/security/analyze` | Seven-layer rules-based UPI risk hints |
| `GET` | `/api/security/layers` | Risk-check layer descriptions (Redis cached) |
| `GET` | `/api/security/scam-education/{type}` | Scam education content |
| `GET`, `POST` | `/api/fraud-reports` | Create/list the caller's fraud reports |
| `GET` | `/actuator/health` | Service health |

Example registration and transfer:

```sh
curl -X POST http://localhost:8088/api/auth/register \
  -H 'content-type: application/json' \
  -d '{"name":"Jordan Davis","email":"jordan@example.com","password":"a-long-demo-password"}'

# Use accessToken from the response
curl -X POST http://localhost:8088/api/transfers \
  -H 'content-type: application/json' \
  -H 'Authorization: Bearer YOUR_ACCESS_TOKEN' \
  -H 'Idempotency-Key: transfer-request-123' \
  -d '{"recipient":"Alex Morgan","amount":25.50}'
```

Transfer/payment operations validate positive two-decimal amounts, lock the wallet row, guard against overdrafts, and write balanced double-entry ledger lines in a database transaction. Idempotency keys make safe retries return the original transaction. Transaction events are sent to Kafka only after database commit; a Kafka outage does not roll back a completed sandbox payment. Errors use a consistent JSON response shape.

## Configuration

- `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` — PostgreSQL connection.
- `APP_JWT_SECRET` — HMAC signing secret, at least 32 characters; set a unique random value outside local development.
- `JWT_EXPIRATION_SECONDS` — JWT lifetime (default 3600).
- `DEMO_INITIAL_BALANCE` — simulated balance credited at registration (default `12840.50`).
- `REDIS_HOST`, `REDIS_PORT` — Redis cache connection.
- `KAFKA_BOOTSTRAP_SERVERS` — Kafka broker address.
- `SERVER_PORT` — Spring Boot HTTP port (default 8080).
