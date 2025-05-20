# 🧪 Kafka + Redis Integration Testing with Spring Boot

This project demonstrates how to build **real integration tests** using:

- ✅ **Kafka** as a messaging system
- ✅ **Redis** as a temporary storage or cache
- ✅ **Spring Boot** for application logic
- ✅ **Testcontainers** to run Kafka and Redis in real Docker containers during tests
- ✅ **GitHub Actions** for automated CI on every push or pull request

---

## ⚙️ What's included?

### 🧩 Services used
- **Kafka** (real, via Testcontainers)
- **Redis** (real, via Testcontainers)
- **Spring Boot** with two core services:
    - `BasicProducerService` → sends messages to Kafka
    - `BasicConsumerService` → consumes Kafka messages and stores them in Redis

### 🔬 Integration tests
- `KafkaIntegrationTest.java`:
    - Sends and receives real Kafka messages
    - Asserts messages are correctly stored in Redis
    - Simulates message loss
    - Validates multiple message persistence

---

## ✅ What does this project verify?

- That **Kafka and Redis work like in production**
- That your business logic is correctly integrated with real infrastructure
- That messages are **delivered, consumed, and persisted** reliably
- That the system behaves correctly in edge cases (e.g., message not delivered)

---

## 🚀 How to run tests locally

1. Make sure **Docker is installed and running**
2. Run the following command:

```bash
mvn clean verify
