# 🚀 First-Time Setup & Local Development Guide

This guide walks you through setting up and running the entire **Budgeting Application** stack on a new computer—including the database, backend, suggestion engine, frontend, and local Jenkins automation.

---

## 📁 Monorepo Structure

```
budgeting-app/
├── apps/
│   ├── backend/               # Spring Boot (Java 17/21) REST API
│   │   ├── docker/Dockerfile
│   │   ├── jenkins/Jenkinsfile
│   │   └── src/main/resources/application.yaml
│   ├── frontend/              # React UI (Tailwind CSS, Recharts)
│   │   ├── docker/Dockerfile
│   │   ├── jenkins/Jenkinsfile
│   │   └── src/utils/url.js
│   └── suggestion-engine/     # Python FastAPI ML microservice (ONNX / PyTorch)
│       ├── docker/Dockerfile
│       ├── jenkins/Jenkinsfile
│       └── suggestion_engine/
├── jenkins/                   # Root-level CI/CD controller & JCasC automation
│   ├── docker-compose.jenkins.yml
│   ├── Dockerfile
│   ├── plugins.txt
│   ├── jenkins.yaml
│   └── .env.jenkins.example
├── docs/                      # Documentation & guides
│   └── FIRST_TIME_SETUP.md
├── compose.yaml               # Production stack (remote VPS)
├── docker-compose.local.yml   # Local full-stack development
└── .env.example               # Application environment template
```

---

## 📋 Prerequisites

Make sure the following tools are installed on your machine:
* **Docker & Docker Compose** (Docker Desktop or Docker Engine + Compose plugin)
* **Git**
* *(Optional for native non-docker development)*:
  * Java 17 or 21 (JDK)
  * Node.js 18+ & npm
  * Python 3.11+

---

## 🛠️ Step 1: Clone the Repository & Configure Environment

1. **Clone the repository:**
   ```bash
   git clone https://github.com/<your-github-username>/budgeting-app.git
   cd budgeting-app
   ```

2. **Create your application `.env` file:**
   ```bash
   cp .env.example .env
   ```
   *The default values in `.env` are pre-configured for local development (local PostgreSQL, sandbox Plaid, etc.). You can customize your Plaid or Mailgun keys as needed.*

---

## 🐳 Step 2: Spin Up the Full Application with Docker (Recommended)

To run all 4 services (Postgres, Spring Boot backend, Python AI suggestion engine, and React frontend with hot-reloading) simultaneously:

```bash
docker compose -f docker-compose.local.yml up -d --build
```

### 🌐 Service Endpoints:
| Service | URL / Port | Description |
| :--- | :--- | :--- |
| **Frontend (React)** | [http://localhost:3000](http://localhost:3000) | Web UI (with live hot-reloading) |
| **Backend (Spring Boot)** | [http://localhost:8080](http://localhost:8080) | REST API & Auth endpoints |
| **Health Check** | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | Backend health status |
| **Suggestion Engine (Python)** | [http://localhost:8000](http://localhost:8000) | ML transaction categorization service |
| **Database (PostgreSQL)** | `localhost:5432` | Database (`user=postgres`, `pass=postgres`, `db=budget-app`) |

### 🔍 Viewing Logs:
```bash
# View logs from all services in real-time
docker compose -f docker-compose.local.yml logs -f

# Or follow a specific service:
docker compose -f docker-compose.local.yml logs -f backend
docker compose -f docker-compose.local.yml logs -f frontend
docker compose -f docker-compose.local.yml logs -f suggestion-engine
```

### 🛑 Stopping the Stack:
```bash
docker compose -f docker-compose.local.yml down
```

---

## ⚡ Step 3: Fast Local Jenkins Setup (JCasC)

If you want to run CI/CD builds locally and deploy to your remote VPS server without manually configuring Jenkins:

1. **Create the Jenkins `.env.jenkins` file:**
   ```bash
   cp jenkins/.env.jenkins.example jenkins/.env.jenkins
   ```

2. **Populate your deployment secrets in `jenkins/.env.jenkins`:**
   * `DOCKER_USER`: Your Docker Hub username
   * `DOCKERHUB_PASSWORD`: Your Docker Hub access token or password
   * `SERVER_IP`: Production VPS IP address (e.g. `<your-vps-ip>`)
   * `SSH_USER`: Remote VPS user (e.g. `deployuser` or `ubuntu`)
   * `DEPLOY_PATH`: Remote deployment directory (e.g. `/home/deployuser`)
   * `SSH_PRIVATE_KEY`: Your OpenSSH private key used to connect to the VPS
   * `JENKINS_ADMIN_USER` & `JENKINS_ADMIN_PASSWORD`: Your desired Jenkins login credentials

3. **Start the Jenkins controller:**
   ```bash
   docker compose -f jenkins/docker-compose.jenkins.yml up -d --build
   ```

4. **Access Jenkins:**
   * Open [http://localhost:8081](http://localhost:8081) in your browser.
   * Sign in with your admin credentials.
   * **All credentials (`docker-user`, `dockerhubpwd`, `server-ip`, `deploy-ssh-key`) and 3 independent pipelines (`budget-app-backend`, `budget-app-frontend`, `budget-app-suggestion-engine`) are automatically loaded and ready to trigger.**

---

## 💻 Alternative: Native Local Development (Without Docker Containers)

If you prefer running services directly on your host machine / IDE:

### 1. Start PostgreSQL
```bash
docker run -d --name budget-postgres -p 5432:5432 -e POSTGRES_DB=budget-app -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:15-alpine
```

### 2. Run the Spring Boot Backend
```bash
cd apps/backend
./gradlew bootRun
```
*`dotenv-java` will automatically read your `.env` file and configure datasource and API credentials.*

### 3. Run the Python Suggestion Engine
```bash
cd apps/suggestion-engine
python -m venv .venv
source .venv/bin/activate
pip install -r suggestion_engine/inference/requirements.txt
uvicorn suggestion_engine.inference.service:app --host 0.0.0.0 --port 8000 --reload
```

### 4. Run the React Frontend
```bash
cd apps/frontend
npm install
npm start
```
*Frontend opens automatically at [http://localhost:3000](http://localhost:3000).*
