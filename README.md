#  Expense Tracker

A full-stack expense management app built with **Spring Boot** and **React**, featuring a hand-rolled OAuth2 Authorization Server + Resource Server (not Auth0/Firebase) implementing the Authorization Code + PKCE flow end-to-end.

** Live App:** [expense-tracker-backend-55wg.onrender.com](https://expense-tracker-backend-55wg.onrender.com)
*(Note: the URL says "backend" from an earlier deployment plan — it now serves both the frontend and API from a single Render deployment.)*

** Repo:** [github.com/ks9205124-cloud/expenseTracker](https://github.com/ks9205124-cloud/expenseTracker)

---

## Why This Project

This was built as a dual-purpose project: to properly learn React (rather than stopping at tutorials) and to get hands-on with a real OAuth2 flow instead of outsourcing auth to a third-party provider. Most developers plug in Auth0 or Firebase and move on — this project forced an understanding of what's actually happening underneath: PKCE code exchange, JWT issuance, filter chains, and role-based access control, all implemented and debugged by hand.

## Development Approach

The application logic — backend architecture, Spring Security configuration, auth flow, database design, and business logic — was designed and written independently. AI assistance was used narrowly on the frontend, for things like Tailwind/flexbox layout suggestions, not for the core logic or security implementation.

---

## Screenshots

> _Add screenshots here, e.g.:_
> `![Login](docs/screenshots/login.png)` · `![Dashboard](docs/screenshots/dashboard.png)`

| Login | Dashboard |
|---|---|
| ![Login screenshot](docs/screenshots/login.png) | ![Dashboard screenshot](docs/screenshots/dashboard.png) |

---

## Features

- **Custom OAuth2 + PKCE Authentication** — self-hosted Spring Authorization Server (not a third-party provider), so the full token issuance/exchange flow is implemented and understood end-to-end, from PKCE code_verifier/challenge generation on the frontend to token exchange and JWT-based session handling on the backend.
- **Role-Based & Method-Level Authorization** — endpoint access restricted via `hasRole`/`hasAuthority`, HTTP-method-specific request matchers, and a custom authentication success handler that routes users by role.
- **User Registration & Login** — full registration flow (working end-to-end) with BCrypt password hashing and default categories auto-seeded for every new user.
- **Category & Expense Management (CRUD)** — full create/read/update/delete for both categories and expenses, with category deletion blocked if expenses still reference it. Amounts are stored as `BigDecimal` for accuracy.
- **Protected Routes** — React Router routes (e.g. `/dashboard`) that block unauthenticated access and clear invalid sessions client-side.
- **Responsive UI** — built with Tailwind CSS.

### 🚧 Planned / In Progress
- Category-wise expense breakdown via pie chart
- Refresh tokens / persistent login (sessions currently end on browser close)
- Proper CSRF token handling on `/login` (currently CSRF-exempted for this path)

### Current Limitations (Honest Assessment)
- **Sessions aren't persistent.** No refresh token flow yet, so closing the browser logs you out — a known gap, not an oversight.
- **CSRF on `/login` is exempted, not solved.** Works correctly but isn't the final intended setup.
- **No spending visualizations yet.** Data is tracked and calculable, but there's no chart/graph view in the UI — the pie chart is next.

---

## Tech Stack

**Frontend:** React (Vite) · Tailwind CSS · React Router v6

**Backend:** Java · Spring Boot · Spring Security (custom OAuth2 Authorization Server + Resource Server) · Spring Data JPA / Hibernate

**Database:** MySQL

**Deployment:** Docker · Render

---

## Getting Started

### Prerequisites
- JDK 17+
- Node.js (v18+)
- Maven
- MySQL (or run via the provided Docker setup)

### Local Setup

```bash
# Clone the repo
git clone https://github.com/ks9205124-cloud/expenseTracker.git
cd expenseTracker

# Backend
./mvnw spring-boot:run

# Frontend (in a separate terminal)
cd frontend
npm install
npm run dev
```

Or run everything together with Docker:

```bash
docker-compose up --build
```

---

## Architecture Notes

The auth layer is intentionally hand-built rather than delegated to a third-party provider (Auth0/Firebase) or a simple hand-rolled JWT filter — it implements a full Spring Authorization Server + Resource Server setup (per *Spring Security in Action*), including:

- Custom `AuthenticationProvider` and `SecurityFilterChain` configuration
- DB-backed `UserDetailsService` with a `User` JPA entity and role-based `Authority` mapping
- Separate, correctly-configured filter chains (and CORS setup) for the Authorization Server and Resource Server
- PKCE `code_verifier` / `code_challenge` generation on the client, with token exchange handled in the OAuth callback

This was a deliberate choice to be able to explain the OAuth2 flow in depth rather than treat auth as a black box.

---

## Project Structure

> Reconstructed from what's been built so far — please check this against the actual repo and correct anything that's drifted, especially exact package names.

```
expenseTracker/
├── src/main/java/.../expensetracker/
│   ├── config/
│   │   ├── SecurityConfig.java            (Resource Server filter chain)
│   │   └── AuthorizationServerConfig.java (OAuth2 Authorization Server, PKCE, registered client)
│   ├── security/
│   │   ├── SecurityUser.java              (implements UserDetails)
│   │   ├── JpaUserDetailsService.java
│   │   ├── CustomAuthenticationSuccessHandler.java
│   │   ├── RequestLoggingFilter.java
│   │   └── AuthenticationLoggingFilter.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Authority.java
│   │   ├── Category.java
│   │   └── Expense.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── CategoryRepository.java
│   │   └── ExpenseRepository.java
│   ├── service/
│   │   ├── UserService.java
│   │   ├── CategoryService.java
│   │   └── ExpenseService.java
│   └── controller/
│       ├── CategoryController.java
│       └── ExpenseController.java
├── frontend/
│   └── src/
│       ├── pages/
│       │   ├── LoginPage.jsx
│       │   ├── CallbackPage.jsx
│       │   ├── DashboardPage.jsx
│       │   └── RegisterPage.jsx
│       └── services/
│           └── authService.js             (PKCE code_verifier/challenge, token exchange)
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## License

MIT (or update as preferred)