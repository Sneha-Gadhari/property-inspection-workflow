# Property Inspection Workflow

Containerized Property Inspection Workflow — a DevOps capstone project. Digitizes the property inspection process end-to-end: request submission → document/data validation → reviewer action → approval/rejection → status tracking.

## Tech Stack
- Java 21, Spring Boot 4.1.0
- Maven (build tool)
- Spring Data JPA + H2 (in-memory DB for local dev)
- Apache Tomcat (embedded)

## Running Locally
```
mvn spring-boot:run
```
App starts at `http://localhost:8081` (port 8080 is reserved for Jenkins).

## Current API Endpoints
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/inspections` | Submit a new inspection request |
| GET | `/api/inspections` | List all inspection requests |
| GET | `/api/inspections/{id}` | Get a single request by ID |
| GET | `/api/inspections/status/{status}` | Filter requests by status |

## Planned DevOps Pipeline
Git/GitHub → Jenkins (CI) → Selenium (quality gate) → Docker (containerize) → Ansible/Puppet (provisioning) → Tomcat (deploy)

## Project Roadmap
See project documentation (Weeks 1–15) for the full 15-week MVP plan, architecture, and DevOps lifecycle.