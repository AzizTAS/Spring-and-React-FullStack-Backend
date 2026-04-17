# Hoaxify — Backend

This is the REST API for Hoaxify, a full-stack e-commerce project I built to practice Spring Boot. It handles everything from user registration and authentication to product catalog management, orders, and payments.

## Tech Stack

- Java 17 / Spring Boot 3.1.2
- Spring Security with BCrypt password hashing
- Token-based authentication — supports both opaque UUID tokens (stored in DB) and stateless JWT
- Spring Data JPA / Hibernate
- H2 for local development, PostgreSQL for production
- SendGrid for transactional emails
- Apache Tika for file type detection
- Maven

## Running Locally

```bash
git clone https://github.com/AzizTAS/Spring-and-React-FullStack-Backend.git
cd Spring-and-React-FullStack-Backend

./mvnw spring-boot:run
```

API will be available at `http://localhost:8080`.

**H2 Console (dev only):** `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./dev.db`
- Username: `sa`
- Password: _(leave empty)_

## Project Structure

```
src/main/java/com/hoaxify/ws/
├── admin/          # Admin statistics and management endpoints
├── auth/           # Login, logout, token handling
├── cart/           # Shopping cart
├── category/       # Product categories
├── configuration/  # Security config, CORS, TokenFilter
├── email/          # SendGrid email integration
├── error/          # Global exception handling
├── file/           # File upload service
├── order/          # Order management
├── payment/        # Payment processing
├── product/        # Product catalog
├── review/         # Product reviews
└── user/           # User registration and profile
```

## Authentication Flow

A `POST /api/v1/auth` request with email and password goes through `AuthService`, which verifies the password with BCrypt and then delegates to `TokenService` to create a token. The response includes the token and user info. On subsequent requests, `TokenFilter` picks up the token from the `Authorization` header or the `hoax-token` cookie and loads the user into the security context.

You can switch token strategies in `application.properties`:

```properties
hoaxify.token-type=opaque   # UUID token stored in the database (default)
# hoaxify.token-type=jwt    # Stateless JWT — also set hoaxify.security.token-key
```

Inactive users (email not yet verified) get a 403 response until they activate their account.

## API Endpoints

| Method | Path | Access |
|--------|------|--------|
| POST | `/api/v1/users` | Public — register |
| POST | `/api/v1/auth` | Public — login |
| DELETE | `/api/v1/auth` | Authenticated — logout |
| GET | `/api/v1/products/**` | Public |
| GET | `/api/v1/categories` | Public |
| PUT | `/api/v1/users/{id}` | Authenticated |
| DELETE | `/api/v1/users/{id}` | Authenticated |
| * | `/api/v1/admin/**` | ADMIN role only |
| * | `/api/v1/cart/**` | Authenticated |
| * | `/api/v1/orders/**` | Authenticated |
| * | `/api/v1/payments/**` | Authenticated |

## Configuration

Key properties to set before running:

```properties
hoaxify.token-type=opaque
sendgrid.api.key=your_sendgrid_key
# For JWT mode only:
hoaxify.security.token-key=your_secret_key
```

## Related

Frontend: [Spring-and-React-FullStack-Frontend](https://github.com/AzizTAS/Spring-and-React-FullStack-Frontend)
