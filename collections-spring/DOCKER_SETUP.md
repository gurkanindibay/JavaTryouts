# Kafka + Spring Boot Application with Docker Compose

This project demonstrates Apache Kafka features integrated with Spring Boot, all running in Docker containers.

## 🚀 Quick Start with Docker Compose

### Prerequisites
- Docker Desktop installed and running
- Docker Compose (included with Docker Desktop)

### Running the Complete Stack

1. **Clone and navigate to the project directory:**
```bash
cd c:\source\Tryouts\JavaTryouts\collections-spring
```

2. **Start all services with Docker Compose:**
```bash
docker-compose up --build
```

This will start:
- **Zookeeper** (port 2181) - Kafka coordination service
- **Kafka** (port 9092) - Message broker
- **Kafka UI** (port 8080) - Web interface for Kafka management
- **Spring Boot App** (port 8081) - Your application with Kafka integration
- **Topic Initialization** - Automatically creates required topics

3. **Wait for all services to be healthy** (approximately 60-90 seconds)

### Accessing the Services

- **Spring Boot Application**: http://localhost:8081
- **Kafka UI Dashboard**: http://localhost:8080
- **Health Check**: http://localhost:8081/actuator/health

## 📊 Kafka UI Dashboard

The Kafka UI provides a web interface to:
- View topics and their messages
- Monitor consumer groups
- See partition assignments
- View message schemas
- Real-time monitoring

Access it at: http://localhost:8080

## 🎯 Testing the Application

### 1. Basic Library Operations (triggers Kafka events)

```bash
# Add a book (triggers BOOK_ADDED event)
curl -X POST http://localhost:8081/api/library/add \
  -H "Content-Type: application/json" \
  -d '{"title":"1984","author":"George Orwell"}'

# List books
curl -X GET http://localhost:8081/api/library/list

# Borrow a book (triggers BOOK_BORROWED event)
curl -X POST "http://localhost:8081/api/library/borrow?title=1984"
```

### 2. Direct Kafka Operations

```bash
# Send custom book event
curl -X POST http://localhost:8081/api/kafka/book-event \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "BOOK_ADDED",
    "bookId": "kafka-book-1",
    "title": "Kafka: The Definitive Guide",
    "author": "Neha Narkhede"
  }'

# Send message to specific partition
curl -X POST "http://localhost:8081/api/kafka/partition/1?message=Hello%20from%20partition%201"

# Send batch messages
curl -X POST "http://localhost:8081/api/kafka/batch/partition-demo?count=5"

# Get Kafka cluster information
curl -X GET http://localhost:8081/api/kafka/info
```

### 3. Monitor in Kafka UI

1. Open http://localhost:8080
2. Navigate to "Topics"
3. Click on `book-events`, `borrow-events`, or `partition-demo`
4. View messages in real-time as you make API calls

## 🔧 Development Commands

### Docker Commands

```bash
# Start services in background
docker-compose up -d

# View logs for all services
docker-compose logs -f

# View logs for specific service
docker-compose logs -f app
docker-compose logs -f kafka

# Stop all services
docker-compose down

# Stop and remove volumes (fresh start)
docker-compose down -v

# Rebuild and restart
docker-compose up --build --force-recreate
```

### Kafka CLI Commands (from within container)

```bash
# Execute commands in Kafka container
docker exec -it kafka bash

# List topics
kafka-topics --bootstrap-server localhost:9092 --list

# Describe a topic
kafka-topics --bootstrap-server localhost:9092 --describe --topic book-events

# Console consumer (listen to messages)
kafka-console-consumer --bootstrap-server localhost:9092 --topic book-events --from-beginning

# List consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 --list

# Describe consumer group
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group library-group
```

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Compose Stack                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │  Zookeeper  │    │    Kafka    │    │   Kafka UI      │  │
│  │   :2181     │◄──►│    :9092    │◄──►│    :8080        │  │
│  └─────────────┘    └─────────────┘    └─────────────────┘  │
│                             ▲                               │
│                             │                               │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │            Spring Boot Application                      │  │
│  │                    :8081                               │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐ │  │
│  │  │  Producer   │  │  Consumers  │  │  REST API       │ │  │
│  │  │  Service    │  │  (Groups)   │  │  Controllers    │ │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────┘ │  │
│  └─────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 📋 Topics and Consumer Groups

### Topics (3 partitions each):
- **book-events**: Book lifecycle events (add, update, remove)
- **borrow-events**: Book borrowing events
- **partition-demo**: Demonstration of partition-specific messaging

### Consumer Groups:
- **library-group**: Main processing group
- **library-group-1**: Analytics processing group
- **library-group-2**: Notification processing group
- **library-group-record-listener**: Record-level message inspection

## 🐛 Troubleshooting

### Common Issues

1. **Port conflicts**:
   - If port 8080 is in use, Kafka UI won't start
   - If port 8081 is in use, the app won't start
   - Change ports in `docker-compose.yml` if needed

2. **Services not starting**:
   ```bash
   # Check service status
   docker-compose ps
   
   # Check logs for errors
   docker-compose logs kafka
   docker-compose logs app
   ```

3. **Kafka connection issues**:
   ```bash
   # Restart Kafka services
   docker-compose restart kafka
   
   # Check if Kafka is healthy
   docker exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092
   ```

4. **Application not connecting to Kafka**:
   - Ensure the `with-kafka` profile is active
   - Check that `SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092` in docker-compose.yml

### Health Checks

```bash
# Application health
curl http://localhost:8081/actuator/health

# Kafka topics
curl http://localhost:8081/api/kafka/info
```

## 🔄 Running Without Docker (Local Development)

If you want to run without Docker:

1. **Start only Kafka with Docker:**
```bash
# Start just Kafka services
docker-compose up zookeeper kafka kafka-ui init-kafka
```

2. **Run Spring Boot locally:**
```bash
# Use the with-kafka profile
mvn spring-boot:run -Dspring-boot.run.profiles=with-kafka
```

3. **Or run without Kafka:**
```bash
# Use the no-kafka profile (default)
mvn spring-boot:run
```

## 📝 Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `no-kafka` | Spring profile (`no-kafka` or `with-kafka`) |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka bootstrap servers |

## 🧪 Testing

Run the integration tests:
```bash
# Tests use embedded Kafka
mvn test
```

## 📚 Learning Resources

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Kafka Reference](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [Kafka UI GitHub](https://github.com/provectus/kafka-ui)

## 🎉 Next Steps

1. Explore the Kafka UI dashboard
2. Try the API endpoints
3. Monitor consumer groups
4. Experiment with partition assignments
5. Add more complex event processing logic
