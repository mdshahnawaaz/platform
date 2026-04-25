# On-Demand Logistics Platform

A full-stack logistics and delivery platform built with Spring Boot, Thymeleaf, MySQL, Redis, Kafka, and optional Python-based ML services.

The project includes:

- A user portal for creating and tracking bookings
- A driver portal for handling assigned deliveries
- An admin dashboard for fleet and booking oversight
- Dynamic pricing and ETA estimation
- AI/ML workflows for demand forecasting and ETA prediction

## Tech Stack

- Java 21
- Spring Boot 3
- Spring MVC + Thymeleaf
- Spring Data JPA
- MySQL
- Redis
- Apache Kafka
- Stripe
- Python/FastAPI-style model serving for ML integrations

## Main Features

- User login and booking flow
- Driver login, booking management, and completion verification
- Admin login and operational dashboard
- Booking pricing and ETA quote generation
- Driver assignment and trip lifecycle tracking
- Contact/email support flow
- Demand snapshot export for model training
- ETA dataset export and live external model scoring

## Project Structure

```text
.
├── src/main/java/com/logistic/platform
│   ├── Configuration/
│   ├── Controllers/
│   ├── Helper/
│   ├── models/
│   ├── repository/
│   └── services/
├── src/main/resources
│   ├── application.properties
│   └── templates/
├── ml/
│   ├── train_demand_model.py
│   ├── train_eta_model.py
│   ├── serve_demand_model.py
│   ├── serve_eta_model.py
│   ├── artifacts/
│   └── README.md
├── data/
├── seed_large_users_drivers.sql
└── pom.xml
```

## Portals and Key Routes

- `/` - landing page
- `/portal/user` - user portal and login
- `/logistics/drivers/portal` - driver portal and login
- `/logistics/admin/login` - admin login
- `/logistics/admin/dashboard` - admin dashboard
- `/logistics/bookings` - booking creation endpoint
- `/logistics/ai/*` - AI and ML operations endpoints

## Prerequisites

Install these locally before running the application:

- Java 21
- Maven
- MySQL 8+
- Python 3.10+ for the `ml/` services

Optional but supported:

- Redis
- Kafka

## Local Setup

### 1. Configure the database

Create a MySQL database named `logistics`.

The application currently reads configuration from [application.properties](/Users/mdshahnawaazansari/Documents/platform/src/main/resources/application.properties). Before running in your own environment, update database, mail, admin, and Stripe settings to match your local setup.

If you want sample data, import:

```bash
mysql -u root -p logistics < seed_large_users_drivers.sql
```

### 2. Run the Spring Boot app

Use Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Or build and run:

```bash
./mvnw clean package
java -jar target/platform-0.0.1-SNAPSHOT.jar
```

By default the app runs on:

```text
http://localhost:8080
```

## AI and ML Integration

The repository includes two optional ML flows:

- Demand forecasting
- ETA prediction

The Java application can export training data and call external Python services for model scoring.

### Demand Forecasting

Key endpoints:

- `/logistics/ai/feature-snapshots`
- `/logistics/ai/feature-snapshots/export.csv`
- `/logistics/ai/model-input`
- `/logistics/ai/model-score`
- `/logistics/ai/model-status`

### ETA Prediction

Key endpoints:

- `/logistics/ai/eta/export.csv`
- `/logistics/ai/eta/schema`
- `/logistics/ai/eta/bookings/{bookingId}/model-input`
- `/logistics/ai/eta/bookings/{bookingId}/model-score`
- `/logistics/ai/eta/model-status`

### Run the ML services

Create a virtual environment and install dependencies:

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r ml/requirements.txt
```

Start the demand model service:

```bash
uvicorn ml.serve_demand_model:app --reload --port 8000
```

Start the ETA model service:

```bash
uvicorn ml.serve_eta_model:app --reload --port 8001
```

The detailed ML workflow is documented in [ml/README.md](/Users/mdshahnawaazansari/Documents/platform/ml/README.md).

## Architecture Notes

This project is designed around a logistics workflow with:

- Spring Boot for business logic and server-rendered UI
- MySQL for persistent booking, user, driver, and package data
- Redis for fast-access operational data
- Kafka for event-driven and real-time location-oriented workflows
- Thymeleaf templates for portal interfaces
- Python services for production-style model training and inference experiments

## Current Functional Areas

- Booking creation and booking state transitions
- Driver assignment and driver dashboard management
- Admin dashboard metrics and oversight
- Email-based delivery verification flow
- Payment checkout integration with Stripe
- Demand data capture for forecasting experiments
- ETA scoring fallback between heuristic and ML-driven predictions

## Known Gaps / Future Improvements

- Move secrets and credentials out of `application.properties` into environment variables
- Add proper authentication/authorization for all roles
- Add automated tests for controllers, services, and ML integration points
- Containerize the stack for simpler local setup
- Add API documentation and example requests
- Merge or expand the frontend into a more polished production UI

## Notes

- The repo currently contains active in-progress work, especially around ETA and AI features.
- Some integrations are optional for local development; the core app centers on the Spring Boot + MySQL flow.
- Redis, Kafka, mail, and Stripe behavior depends on your local configuration.
