# MaVille — Gestion des Travaux Publics de Montréal

> Full-stack application connecting residents, service providers, and city agents for civic issue management and public works oversight in Montreal.

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1.4-6DB33F?logo=spring&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-010101?logo=socket.io)
![JWT](https://img.shields.io/badge/Auth-JWT-black?logo=jsonwebtoken)

---

## Overview

MaVille is a three-role platform that streamlines the lifecycle of civic issue management: residents report problems, service providers bid on solutions, and STPM agents prioritize, assign, and supervise projects. Built with a modern full-stack architecture and real-time capabilities.

**Roles:**
- 👤 **Resident** — Report problems, track public works, subscribe to neighbourhoods/streets, receive real-time notifications
- 🛠️ **Service Provider** — Browse problem sheets, submit proposals, manage ongoing projects, subscribe to relevant categories
- 🏛️ **STPM Agent** — Monitor signals in real time, assign priorities, review proposals, accept/reject with reasoning, link signals to existing problem sheets

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 17, Spring Boot 3.1.4, Spring Security, Spring Data JPA, Spring WebSocket |
| **Frontend** | React 19, Vite 8, React Router v7, StompJS + SockJS, Lucide Icons |
| **Database** | PostgreSQL |
| **External API** | Montreal Open Data (`donnees.montreal.ca`) — real public works data |
| **Auth** | JWT (stateless), role-based access control |
| **Real-time** | STOMP over WebSocket with JWT-authenticated channels |
| **Deployment** | Docker, multi-stage build |
| **Testing** | JaCoCo code coverage, Maven `verify` lifecycle |
| **Client** | OkHttp-based REST client + CLI and Swing GUI modes |

---

## Architecture

### Backend Layered Design

```
┌─────────────────────────────────────────────────────┐
│                   Controllers (REST)                 │
│  Auth │ Problem │ Project │ Subscription │ Travaux   │
│  Notification │ Provider │ Resident                  │
└──────────────┬──────────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────────┐
│                  Services (Business Logic)           │
│  ServiceProblem │ ProjectService │ NotificationService│
│  WebSocketNotificationService                       │
└──────────────┬──────────────────────────────────────┘
               │
┌──────────────▼──────────────────────────────────────┐
│          Data Access (JPA Repositories)              │
│  ProblemRepository │ ProjectRepository │ ...          │
└──────────────┬──────────────────────────────────────┘
               │
        ┌──────▼──────┐
        │  PostgreSQL  │
        └─────────────┘
```

### Key Design Decisions

- **Stateless JWT Authentication** — Spring Security filter chain validates tokens on every request; no session state
- **WebSocket with JWT** — Custom `ChannelInterceptor` validates JWT on STOMP `CONNECT` frame via `X-Authorization` header
- **Open Session In View** — Lazy-loading of JPA associations during serialization without `@Transactional` on every controller
- **Multi-mode Client** — `MavilleRestClient` (OkHttp) shared between Swing GUI and CLI; React frontend uses native `fetch`

### Real-Time Notification Flow

```
Resident reports a problem
        │
        ▼
NotificationService.save()
        │
        ▼
WebSocketNotificationService.push()
        │
        ▼
SimpMessagingTemplate.convertAndSend()
        │
        ├──► /topic/notifications/resident/{email}
        └──► /topic/notifications/admin/{email}
```

---

## Features by Role

### Resident
- Report a problem in their neighbourhood (street, type, description)
- View all current & upcoming public works in Montreal (from internal DB or Montreal Open Data API)
- Filter projects by neighbourhood, work type, and priority
- Subscribe to specific neighbourhoods or streets for real-time alerts
- Real-time push notifications for new/updated projects in subscribed areas
- Dashboard with personal stats (signal count, nearby projects, active subscriptions)

### Service Provider
- Browse all problem sheets created by STPM agents
- Submit a proposal (bid) on a problem sheet
- Real-time tracking of STPM decision (accept/reject with reason)
- View and manage all assigned projects (modify description, dates, status transitions)
- Subscribe to neighbourhoods or problem types for resident signal alerts
- Real-time notifications for new problem sheets matching their subscriptions

### STPM Agent
- Real-time monitoring of all resident signals with summary statistics
- Assign a priority level (HIGH / MEDIUM / LOW) to create a problem sheet
- Refuse signals with optional reason
- **Link a raw signal to an existing problem sheet** (merge related reports)
- Review all provider proposals in real time
- Accept a proposal (creates a project automatically)
- Reject a proposal with a mandatory reason
- Real-time notifications for every new signal and proposal submission

---

## API Endpoints

### Authentication & Users
| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/login` | Authenticate, returns JWT |
| GET | `/api/auth/me` | Current user profile |
| GET | `/api/users/me` | Legacy identity endpoint |

### Problems (Signals & Problem Sheets)
| Method | Path | Description |
|---|---|---|
| POST | `/api/problems/report_problem` | Resident reports a problem |
| GET | `/api/problems/my_reported_problems?residentId=` | Resident's own reports |
| GET | `/api/problems/all_reported_problems` | All signals (admin) |
| GET | `/api/problems/problems_not_assigned` | Unprocessed signals |
| GET | `/api/problems/problems_assigned` | Prioritized problem sheets |
| POST | `/api/problems/assign_problem_priority?problemId=&priorite=` | Assign priority |
| POST | `/api/problems/link_signal?signalId=&parentProblemId=` | Link signal to existing sheet |

### Projects (Proposals & Work Orders)
| Method | Path | Description |
|---|---|---|
| POST | `/api/projects/submit` | Submit a proposal |
| GET | `/api/projects/my-proposals?providerCompanyNumber=` | Provider's proposals |
| GET | `/api/projects/submitted` | All submitted proposals (admin) |
| POST | `/api/projects/{id}/accept` | Accept a proposal |
| POST | `/api/projects/{id}/reject?reason=` | Reject with reason |
| PUT | `/api/projects/{id}/description` | Update description |
| PUT | `/api/projects/{id}/end-date` | Update end date |
| PUT | `/api/projects/{id}/start` | Start work |
| PUT | `/api/projects/{id}/delay` | Delay work |
| PUT | `/api/projects/{id}/resume` | Resume delayed work |
| PUT | `/api/projects/{id}/complete` | Complete with actual cost |
| GET | `/api/projects/filter?neighbourhood=&type=&priority=&status=` | Multi-criteria filter |
| GET | `/api/projects/my` | Authenticated user's projects |

### Subscriptions & Notifications
| Method | Path | Description |
|---|---|---|
| POST | `/api/subscriptions/residents` | Create resident subscription |
| POST | `/api/subscriptions/providers` | Create provider subscription |
| GET | `/api/subscriptions/residents?residentId=` | Resident subscriptions |
| GET | `/api/subscriptions/providers?companyNumber=` | Provider subscriptions |
| GET | `/api/notifications?userId=&userType=` | Get notifications |
| GET | `/api/notifications/unread?userId=&userType=` | Unread count |
| PUT | `/api/notifications/{id}/read` | Mark as read |

### Montreal Open Data (Travaux)
| Method | Path | Description |
|---|---|---|
| GET | `/api/travaux` | Fetch all public works from Montreal API |
| GET | `/api/travaux/filter?filterKey=&filterValue=` | Filter external data |
| DELETE | `/api/travaux` | Clear cached data |

---

## Getting Started

### Prerequisites
- Java 17+
- Node.js 18+ (for frontend)
- Docker (optional)

### Backend

```bash
# Compile
mvn clean compile

# Run server (Swing GUI also launches)
mvn exec:java -Dexec.mainClass="ca.udem.maville.Main"

# Run CLI mode (separate terminal)
mvn exec:java -Dexec.mainClass="ca.udem.maville.Main" -Dexec.args="--cli"
```

### Frontend

```bash
cd maville-frontend
npm install
npm run dev     # Development server on port 5173
npm run build   # Production build
```

### Docker

```bash
docker build -t maville-app .
docker run -d -p 7070:7070 --name maville-container maville-app
```

---

## Testing

```bash
mvn clean verify
```

Generates a JaCoCo coverage report at `target/site/jacoco/index.html`.

---

## Project Structure

```
├── src/main/java/ca/udem/maville/
│   ├── api/          # REST client (OkHttp) + Montreal API datasource
│   ├── controller/   # REST controllers (Problem, Project, Subscription, etc.)
│   ├── config/       # WebSocket config + STOMP auth interceptor
│   ├── security/     # JWT auth, Spring Security, AuthController
│   ├── services/     # Business logic (problem, project, notification)
│   ├── model/        # JPA entities (Problem, Project, Resident, etc.)
│   ├── repository/   # Spring Data JPA repositories
│   ├── dto/          # Request/response DTOs
│   ├── cli/          # Command-line interface
│   └── gui/          # Swing graphical interface
├── maville-frontend/ # React 19 + Vite frontend
│   ├── src/pages/    # Role-based pages (resident/, admin/, provider/)
│   ├── src/components/ # Reusable components (ProblemCard, Proposal, etc.)
│   ├── src/context/  # AuthContext + WebSocketContext
│   └── src/api/      # API client (fetch-based)
├── Dockerfile
└── pom.xml
```

---

## Highlights for Interviewers

- **Full-stack proficiency** — Java/Spring Boot backend + React 19 frontend with Vite
- **Real-time systems** — STOMP WebSocket with JWT-authenticated channels for live notifications
- **External API integration** — Montreal Open Data REST API consumed and cached
- **Role-based auth** — JWT with Spring Security, per-role API and UI access control
- **Database design** — JPA entities with self-referencing relationships (problem hierarchy), enums for type safety
- **Containerization** — Multi-stage Docker build for production deployment
- **Multi-client architecture** — REST API consumed by React, Swing GUI, and CLI via shared `MavilleRestClient`
- **CI-ready** — Maven lifecycle with JaCoCo test coverage reporting
