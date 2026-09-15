# Zenve Admin CRM — Backend

A Java Spring Boot backend for the `zenve-admin-frontend` React app (doctor
registration review CRM). It implements exactly the API surface the frontend
calls in `src/lib/api.js`.

## Stack

- Java 17, Spring Boot 3.3
- Spring Web, Spring Security (stateless JWT auth), Spring Data JPA
- MySQL (production) **or** H2 file database (zero-setup local dev) — switchable
  with one env var, no code changes
- JJWT for token signing/verification
- Maven

## Getting started

Requires Java 17+ and Maven. No database setup needed by default — it uses an
embedded H2 file database out of the box.

```bash
cd zenve-admin-backend
cp .env.example .env   # optional — every value already has a sane default
mvn spring-boot:run
```

The API comes up on `http://localhost:5000/api`, which matches the frontend's
`.env` default of `VITE_API_URL=http://localhost:5000/api`.

On first boot it seeds:
- Admin login: `admin@zenve.in` / `Admin@123`
- 3 demo doctors (one pending, one approved, one rejected) with matching
  notifications, so the Dashboard/Doctors/Notifications pages aren't empty.

### Switching to MySQL

Set `DB_DRIVER=mysql` (plus `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`,
`DB_PASSWORD` as needed) and the backend will connect to a real MySQL server
instead, creating/updating the `vetcare` schema automatically. See
`database/schema.sql` for the reference DDL if you'd rather provision it by
hand. Every repository call goes through Spring Data JPA, so nothing else
needs to change when you switch drivers — this mirrors the "same `vetcare`
database" architecture the frontend's Database page describes.

## Environment variables

See `.env.example` for the full list with defaults. Highlights:

| Variable | Default | Purpose |
|---|---|---|
| `PORT` | `5000` | HTTP port |
| `DB_DRIVER` | `h2` | `h2` (embedded file db) or `mysql` |
| `DB_HOST`/`DB_PORT`/`DB_NAME`/`DB_USER`/`DB_PASSWORD` | — | MySQL connection (when `DB_DRIVER=mysql`) |
| `DB_FILE_PATH` | `./data/vetcare` | H2 file location (when `DB_DRIVER=h2`) |
| `JWT_SECRET` | dev default | **Change this in production.** Hashed to a 256-bit key internally. |
| `JWT_EXPIRATION_MS` | `86400000` (24h) | Token lifetime |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Comma-separated list of allowed frontend origins |
| `SEED_ADMIN_EMAIL` / `SEED_ADMIN_PASSWORD` / `SEED_ADMIN_NAME` | `admin@zenve.in` / `Admin@123` / `Zenve Admin` | Seeded admin account |

## API reference

All routes are under `/api`. Protected routes require `Authorization: Bearer <token>`.

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/auth/login` | — | `{ email, password }` → `{ token, admin }` |
| GET | `/auth/me` | ✓ | → `{ admin }` |
| GET | `/doctors?status=pending\|approved\|rejected\|all` | ✓ | → `{ doctors, counts }` |
| POST | `/doctors/{id}/approve` | ✓ | Approves a doctor, creates a notification |
| POST | `/doctors/{id}/reject` | ✓ | `{ reason }` → rejects a doctor, creates a notification |
| POST | `/doctors/register` | — | Bonus/public: lets a doctor submit a registration (for end-to-end testing) |
| GET | `/notifications` | ✓ | → `{ notifications, unreadCount }` |
| POST | `/notifications/{id}/read` | ✓ | Marks one notification read |
| POST | `/notifications/read-all` | ✓ | Marks all notifications read |
| GET | `/database` | ✓ | Connection status, driver, size/last-write (h2) or host (mysql), record counts |

Error responses are always `{ "message": "..." }`, matching what
`src/lib/api.js` expects (`data.message`).

## Project layout

```
src/main/java/com/zenve/admin/
  config/       Spring config: security, CORS, JWT/seed/database properties, data seeder
  security/     JWT issuing/parsing + the auth filter
  model/        JPA entities (Admin, Doctor, Notification) + enums
  repository/   Spring Data JPA repositories
  dto/          Request/response records matching the frontend's JSON shape exactly
  service/      Business logic (auth, doctors, notifications, database status)
  controller/   REST controllers
  exception/    ApiException + a global handler that returns { message }
```

## Notes

- This project wasn't compiled inside the sandbox that generated it (no
  outbound access to Maven Central from that environment) — run `mvn compile`
  or `mvn spring-boot:run` locally to build it. The code was written and
  reviewed carefully against Spring Boot 3.3 / JJWT 0.12.x APIs.
- CORS defaults to `http://localhost:5173` (Vite's dev server port) — update
  `CORS_ALLOWED_ORIGINS` for other environments.
