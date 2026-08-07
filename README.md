# Full-Stack Expense Tracker

A full-stack, secure expense management application built with React, Spring Boot, and OAuth2 with PKCE authentication.

---

## Features

- **OAuth2 & PKCE Authentication:** Secure login flow using Spring Security, JWT token issuance, and server-side session cleanup.
- **Client-Side Route Guarding:** Protected React routes (`/dashboard`) that block unauthenticated users and clear invalid sessions.
- **Dynamic User Personalization:** Instant parsing of JWT claims to greet users and customize dashboard views.
- **Category & Expense Management:** Full CRUD capability for tracking spending by category with auto-calculating totals and dynamic filters.
- **Responsive UI:** Clean interface engineered with Tailwind CSS.

---

## Tech Stack

### Frontend
- **Framework:** React + Vite
- **Styling:** Tailwind CSS
- **Routing:** React Router v6

### Backend
- **Framework:** Java / Spring Boot
- **Security:** Spring Security (OAuth2 Authorization Server + Resource Server)
- **Database:** H2 / PostgreSQL (JPA/Hibernate)

---

## Getting Started

### Prerequisites
- Node.js (v18+)
- JDK 17+
- Maven

### Installation & Local Setup

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/ks9205124-cloud/expenseTracker.git](https://github.com/ks9205124-cloud/expenseTracker.git)
   cd expenseTracker