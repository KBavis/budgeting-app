# budgeting-app

Budgeting platform with a Spring Boot backend and React frontend, facilitating users to manage their finances by connecting to their financial institutions to track transactions, allocate budgets, and analyze spending habits.

**URL:**  
[https://bavisbudgeting.com/](https://bavisbudgeting.com/)

## Local Run Configuration

**NOTE**: You should have a Plaid API account (https://dashboard.plaid.com/signup/) and have a PostgresDB that you can spin up locally 

1. Clone the repository to a directory you created locally: `https://github.com/KBavis/budgeting-app.git`
2. Change your working directory to `<repository-directory>/backend/src`
3. Run the following command via command line: `mkdir resources`
4. Create your `application.yaml` file in the `resources` directory with the following format:

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
    baseUrl: <your-plaid-base-url>  # e.g., https://production.plaid.com
    client-id: <your-plaid-client-id>
    secret-key: <your-plaid-secret-key>
#    baseUrl: <your-development-plaid-base-url>
#    client-id: <your-development-plaid-client-id>
#    secret-key: <your-development-plaid-secret-key>

logging:
  level:
    org.springframework.security: DEBUG
    <your-application-package>: DEBUG
```

6) Change directories to `<repository-directory>/backend`
7) Run the following command via command line: `./gradlew bootRun`
8) Verify that application has started successfully 
9) Change your working working directory to be `<repository-directory>/frontend `
10) Run `npm install` and wait for the necessary dependencies to install
11) Run `npm start`
12) Navigate to `http://localhost:3000` and begin using application locally!


## Current Project Status 

### Current Version

- **Version:** `1.0.0`
- **Status:** The application is functional but not complete. It is currently planned to be shelved temporarily to focus on other applications.

### Upcoming Features in Version `1.1.0`

- **Rules.java:** Addition of a feature that enables users to set up a set of rules to automatically assign similar transactions to their appropriate categories.
- **Plaid Webhooks**: Utilization of PlaidAPI webhooks to correctly perform synchronizations of Transactions automatically when available
- **Styling Enhancements**: Updates of styling within our Category Type page, Home Page, and our Budget Summary pages
- **Mobile Friendly**: Ability to utilize application via Phone rather than Computer

## Areas Needing Improvement in V1.0.0 
#### Note: There is a HUGE number of areas that I can improve in, but here are some major points sticking out:
- Larger consideration in creating **reusable components** in React via properties and configurations rather than hard-coding.
- Utilization of TestHelper classes within our Java Unit tests, ensuring our data setup is not clogging actual unit tests
- Single assertions per unit test, making them more legible
- Utilization of HashiCorp Vault, allowing storing of sensitive information
- No longer directly pushing to `main`, but to push to a `/feature`, `/refactor`, or `/docs` branch type with a corresponding Jira Ticket number 
