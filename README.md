# 🧾 budgeting-app 

A full-stack **personal finance management platform** built with **Spring Boot**, **React**, and **PostgreSQL**, enabling users to connect to over **11,000 financial institutions** securely via **Plaid API** to automatically track spending, analyze habits, and intelligently allocate budgets.

🌐 **Live Application:** [https://bavisbudgeting.com](https://bavisbudgeting.com)

> 🔒 **Security Note:** All sensitive financial data is handled securely via Plaid. The application does not store or access any users’ bank credentials or sensitive information. Users can also run the application locally to ensure full control over their data.

---

## 🚀 Project Overview

Empowers users to take control of their financial health through intelligent automation and rich analytics.
It securely links to users’ financial institutions to fetch real-time transactions and account balances, allowing for dynamic budgeting, spending trend visualization, and predictive transaction categorization.

### ✨ Key Features

* **Real-Time Bank Integration:**
  Seamlessly connect financial accounts using the **Plaid API**, fetching transaction and balance data in real time.

* **AI Suggested Transaction Categorization (ONNX + PyTorch):**
  Built custom **neural network models** per user to learn their unique categorization habits.
  Converted models to **ONNX Runtime** for lightweight inference, reducing memory consumption and enabling deployment on a **1 GB Linode instance**—cutting infrastructure costs dramatically while maintaining scalability.

* **Data Visualization & Reporting:**
  Detailed analytics dashboards displaying category performance, monthly spending summaries, and projected savings, built using **Recharts** and **Tailwind CSS**.

* **Modern DevOps & Scalability:**

  * CI/CD via **Jenkins**
  * Containerized with **Docker**
  * Deployed on **Linode VPS**
  * Spring Boot + PostgreSQL backend serving a React frontend

---

## 🧠 Technical Highlights

| Stack                | Description                                               |
| -------------------- | --------------------------------------------------------- |
| **Frontend**         | React (with Tailwind CSS for styling)                     |
| **Backend**          | Spring Boot (Java 17)                                     |
| **Database**         | PostgreSQL                                                |
| **AI/ML**            | PyTorch → ONNX Runtime for per-user lightweight inference |
| **Integration**      | Plaid API for financial data aggregation                  |
| **CI/CD**            | Jenkins automated pipeline                                |
| **Containerization** | Dockerized services                                       |
| **Hosting**          | Deployed on Linode 5 GB RAM instance                      |

---

## ⚙️ Local Setup Guide

> 💡 **Prerequisites**
>
> * Java 17 or later
> * Node.js 16+
> * PostgreSQL installed and running
> * A [Plaid API account](https://dashboard.plaid.com/signup)
> * Git, npm, and Gradle (`./gradlew`)

Users who are concerned about security are encouraged to follow this local setup to run the application fully on their own machine.

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/budgeting-app.git
cd budgeting-app
```

---

### 2. Configure Environment

Copy the `.env.example` template:
```bash
cp .env.example .env
```
*The default values in `.env` are pre-configured for local development.*

---

### 3. Option A: Run Full Stack with Docker (Recommended)

Start all services (Postgres, Backend, Suggestion Engine, and Frontend with live hot-reloading) in one command:

```bash
docker compose -f docker-compose.local.yml up -d --build
```

Access your application at:
* **Frontend**: [http://localhost:3000](http://localhost:3000)
* **Backend API**: [http://localhost:8080](http://localhost:8080)
* **Suggestion Engine**: [http://localhost:8000](http://localhost:8000)

For complete setup instructions (including local Jenkins automation), see the [First-Time Setup Guide](docs/FIRST_TIME_SETUP.md).

---

### 4. Option B: Native Setup (Without Docker)

#### A. Backend (Spring Boot)
```bash
cd apps/backend
./gradlew bootRun
```

#### B. Frontend (React)
```bash
cd apps/frontend
npm install
npm start
```

#### C. Suggestion Engine (Python FastAPI)
```bash
cd apps/suggestion-engine
python -m venv .venv
source .venv/bin/activate
pip install -r suggestion_engine/inference/requirements.txt
uvicorn suggestion_engine.inference.service:app --host 0.0.0.0 --port 8000
```


## 📦 Current Version

* **Version:** `1.0.0`
* **Status:** Functional and deployed
* **Next Version (v1.1.0):** Planned improvements include:

  * Plaid webhook support for live transaction sync
  * Enhanced performance and automation regarding Transaction categorization

---

## 🧩 Architecture Overview

```
┌──────────────────────────────────────────────────────────┐
│                      React Frontend                      │
│      Tailwind CSS • Recharts • Context API • Hooks        │
└──────────────┬───────────────────────────────────────────┘
               │ REST API (JSON)
┌──────────────▼───────────────────────────────────────────┐
│                  Spring Boot Backend                     │
│ Controllers → Services → Repositories → PostgreSQL DB     │
│ ONNX Runtime Models for per-user categorization           │
└──────────────┬───────────────────────────────────────────┘
               │
┌──────────────▼───────────────────────────────────────────┐
│                   External Integrations                  │
│                Plaid API • Jenkins • Docker               │
└──────────────────────────────────────────────────────────┘
```
