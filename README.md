# GMMX Backend - Gym Management Operating System

The robust Spring Boot backend for the GMMX SaaS platform. It handles multi-tenancy, biometric attendance logic, role-based access control (RBAC), and automated CRM workflows.

## 🚀 Tech Stack
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Security**: Spring Security + JWT (JSON Web Tokens)
- **Database**: PostgreSQL
- **Migration**: Flyway
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Containerization**: Docker

---

## 📖 API Documentation

The API uses **Bearer Authentication**. Include the JWT token in the `Authorization` header:
`Authorization: Bearer <your_token>`

### 🔐 Authentication (`/auth`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/auth/register` | Register a new Gym Owner and Tenant (Atomic) |
| `POST` | `/auth/login` | Authenticate user and receive Access/Refresh tokens |
| `POST` | `/auth/google` | Google OAuth2 login/registration |
| `POST` | `/auth/refresh` | Generate new Access Token using Refresh Token |
| `POST` | `/auth/send-otp` | Generate and send OTP (Email/SMS) |
| `POST` | `/auth/verify-otp` | Verify OTP for phone/email verification |
| `GET` | `/auth/check-email` | Check if an email is already registered |

### 🏢 Tenant & Public (`/api/tenants`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/tenants/lookup/{slug}` | Public lookup for gym details by subdomain |
| `GET` | `/api/tenants/check-slug/{slug}` | Check if a gym subdomain is available |

### 📊 Dashboard (`/api/dashboard`)
| Method | Endpoint | Role | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/dashboard/owner/stats` | `OWNER` | High-level metrics for the owner |
| `GET` | `/api/dashboard/owner/recent-activity` | `OWNER` | Latest events (check-ins, payments) |

### 👥 Member Management (`/api/members`)
| Method | Endpoint | Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/members` | `OWNER` | Register a new member |
| `GET` | `/api/members` | `OWNER`, `TRAINER` | List all members (paginated) |
| `GET` | `/api/members/{id}` | `OWNER`, `TRAINER` | Get specific member profile |
| `PUT` | `/api/members/{id}` | `OWNER` | Update member details |
| `DELETE` | `/api/members/{id}` | `OWNER` | Remove member from system |

### 🏋️ Trainer Management (`/api/trainers`)
| Method | Endpoint | Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/trainers` | `OWNER` | Add a new trainer |
| `GET` | `/api/trainers` | `OWNER` | List all trainers |
| `GET` | `/api/trainers/{id}` | `OWNER` | Get trainer details |
| `PUT` | `/api/trainers/{id}` | `OWNER` | Update trainer info |
| `DELETE` | `/api/trainers/{id}` | `OWNER` | Remove trainer |

---

## 🛠️ Development Setup

1. **Clone the repository**
2. **Configure Environment**: Create a `.env` file or use `application-dev.yml`.
3. **Database**: Ensure a PostgreSQL instance is running.
4. **Run Application**:
   ```bash
   ./mvnw spring-boot:run
   ```

### Swagger UI
When running locally, access the interactive API documentation at:
`http://localhost:8080/swagger-ui.html`

On Production:
`https://api.gmmx.app/swagger-ui.html`

---

## 🤝 Contribution

We welcome contributions! To contribute:
1. **Fork** the repository.
2. **Create a Branch** (`git checkout -b feature/AmazingFeature`).
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`).
4. **Push** to the Branch (`git push origin feature/AmazingFeature`).
5. **Open a Pull Request**.

Please ensure your code follows the existing style guidelines and includes necessary tests.

---

## 📄 License

Distributed under the **MIT License**. See `LICENSE` for more information.

© 2026 GMMX Technologies.
