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
git clone https://github.com/KBavis/budgeting-app.git
cd budgeting-app
```

---

### 2. Backend Setup (Spring Boot)

```bash
cd backend/src
mkdir resources
```

Create a `application.yaml` inside the newly created `resources` directory:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://<your-postgresql-url>:5432/<your-database-name>
    username: <your-database-username>
    password: <your-database-password>
    driver-class-name: org.postgresql.Driver

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update

  categories:
    Wants: <your-wants-percentage>
    Needs: <your-needs-percentage>
    Investments: <your-investments-percentage>

plaid:
  api:
    baseUrl: <your-plaid-base-url>  # e.g. https://production.plaid.com
    client-id: <your-plaid-client-id>
    secret-key: <your-plaid-secret-key>

logging:
  level:
    org.springframework.security: DEBUG
    com.bavisbudgeting: DEBUG

suggestion-engine:
  base-url: "http://localhost:8000/"

cors:
  allowed-origins: "http://localhost:3000
```

Then build & start up Spring Boot service:

```bash
cd ../backend
./gradlew build 
./gradlew bootRun
```

Verify that the Spring Boot service starts successfully.

---
### 4. Frontend Setup (React)

```bash
cd ../frontend
npm install
npm start
```

Your application should now be running locally at:
👉 **[http://localhost:3000](http://localhost:3000)**

---
### 5. Onboard New Account 

Navigate to **[http://localhost:3000](http://localhost:3000)** and work through registration process
to setup necessary budgets and allocations

---
### 6. Suggestion Setup (AI/ML)

In order to utilize the Suggestion Engine functionality provided by this code, it's suggested to have at have *at least 50 transactions* categorized manually in order for your model to make accurate suggestions

Once complete, complete following steps: 

a) Create virtual env and install relevant dependencies
```bash
python -m venv backend/suggestion_engine/training/.venv && \
source backend/suggestion_engine/training/.venv/bin/activate && \
pip install -r backend/suggestion_engine/training/requirements.txt
```

b) Train user specific neural network for making predictions
```bash
cd backend 
source suggestion_engine/training/.venv/bin/activate
python suggestion_engine/scripts/nightly_training.py
```

c) Validate model created in following location 
```bash
suggestion_engine/artifacts/{user_id}/model.onnx
```

d) Create new virtual env for inference 
```bash
python -m venv backend/suggestion_engine/inference/.venv && \
source backend/suggestion_engine/inference/.venv/bin/activate && \
pip install -r backend/suggestion_engine/inference/requirements.txt
```

e) Start up FastAPI Service 
```bash
cd backend
source suggestion_engine/inference/.venv/bin/activate
uvicorn suggestion_engine.inference.service:app --host 0.0.0.0 --port 8000
```

The suggestion capabilities should now be available while syncing transactions
---

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
