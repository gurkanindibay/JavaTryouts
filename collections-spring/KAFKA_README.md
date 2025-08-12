# Kafka Features Demo

This Spring Boot application demonstrates various Apache Kafka features including producers, consumers, consumer groups, partitions, and brokers running in a complete Docker environment.

## Prerequisites

1. **Docker Desktop** installed and running
2. **Docker Compose** (included with Docker Desktop)
3. **Java 21+** (for local development)
4. **Maven 3.6+** (for local development)

## 🚀 Quick Start with Docker Compose

### Running the Complete Stack

The easiest way to run this application is using Docker Compose, which provides a complete Kafka environment:

```bash
# Start all services (first run may take 3-5 minutes to download images)
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

This single command starts:
- **Zookeeper** (port 2181) - Kafka coordination service
- **Kafka Broker** (port 9092) - Message broker
- **Kafka UI** (port 8080) - Web management interface
- **Spring Boot App** (port 8081) - Your application with Kafka integration
- **Topic Initialization** - Automatically creates required topics with 3 partitions each

### Accessing the Services

- **🌐 Spring Boot Application**: http://localhost:8081
- **📊 Kafka UI Dashboard**: http://localhost:8080
- **❤️ Health Check**: http://localhost:8081/actuator/health

### Stopping the Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (fresh start)
docker-compose down -v
```

## Alternative: Local Kafka Setup

If you prefer to run Kafka locally instead of using Docker:

### Download and Start Kafka

1. Download Kafka from [Apache Kafka Downloads](https://kafka.apache.org/downloads)
2. Extract the archive
3. Navigate to the Kafka directory

### Start Kafka Services

```powershell
# Start Zookeeper (in one terminal)
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# Start Kafka Server (in another terminal)
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

### Create Topics

```powershell
# Create book-events topic with 3 partitions
.\bin\windows\kafka-topics.bat --create --topic book-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# Create borrow-events topic with 3 partitions
.\bin\windows\kafka-topics.bat --create --topic borrow-events --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# Create partition-demo topic with 3 partitions
.\bin\windows\kafka-topics.bat --create --topic partition-demo --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### Run Application Locally

```bash
# Build the project
mvn clean compile

# Run with Kafka profile
mvn spring-boot:run -Dspring-boot.run.profiles=with-kafka

# Or run without Kafka (for testing other features)
mvn spring-boot:run -Dspring-boot.run.profiles=no-kafka
```

## Kafka Features Demonstrated

### 1. **Producer Features**
- **Async Message Sending**: Non-blocking message publishing with callbacks
- **Custom Key Partitioning**: Messages with same key go to same partition
- **Partition-Specific Sending**: Direct partition assignment
- **Batch Message Processing**: Efficient bulk message sending
- **Acknowledgment Handling**: Delivery confirmation callbacks

### 2. **Consumer Features**
- **Multiple Consumer Groups**: Different groups process same messages independently
- **Manual Acknowledgment**: Explicit message acknowledgment control
- **Partition-Specific Consumption**: Listen to specific partitions only
- **Offset Management**: Start consuming from specific offsets
- **Concurrent Processing**: Multiple consumer threads within same group

### 3. **Consumer Groups**
- **library-group**: Main processing group
- **library-group-1**: Analytics processing group
- **library-group-2**: Notification processing group
- **library-group-record-listener**: Record-level message inspection

### 4. **Partition Strategy**
- **Round-robin**: Default partition assignment
- **Key-based**: Messages with same key go to same partition
- **Custom**: Manual partition assignment

## Running the Application

### Docker Compose (Recommended)

```bash
# Start the complete environment
docker-compose up --build

# Wait for all services to be healthy (60-90 seconds)
# Check status
docker-compose ps

# View logs
docker-compose logs -f app
```

### Local Development

```bash
# Build the project
mvn clean compile

# Run with Kafka (requires local Kafka setup)
mvn spring-boot:run -Dspring-boot.run.profiles=with-kafka

# Run without Kafka (for testing other features)
mvn spring-boot:run -Dspring-boot.run.profiles=no-kafka
```

## API Endpoints

### Book Management (with Kafka Integration)

**PowerShell Examples:**
```powershell
# Add a book (triggers Kafka event)
Invoke-RestMethod -Uri "http://localhost:8081/api/books" -Method POST -ContentType "application/json" -Body '{"title":"The Great Gatsby","author":"F. Scott Fitzgerald"}'

# List all books
Invoke-RestMethod -Uri "http://localhost:8081/api/books" -Method GET

# Borrow a book (triggers Kafka event)
Invoke-RestMethod -Uri "http://localhost:8081/api/books/borrow/The Great Gatsby" -Method POST
```

**cURL Examples:**
```bash
# Add a book (triggers Kafka event)
curl -X POST http://localhost:8081/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"The Great Gatsby","author":"F. Scott Fitzgerald"}'

# List all books
curl -X GET http://localhost:8081/api/books

# Borrow a book (triggers Kafka event)
curl -X POST "http://localhost:8081/api/books/borrow/The%20Great%20Gatsby"
```

### Direct Kafka Operations

**PowerShell Examples:**
```powershell
# Get Kafka information
Invoke-RestMethod -Uri "http://localhost:8081/api/kafka/info" -Method GET

# Send custom book event
Invoke-RestMethod -Uri "http://localhost:8081/api/kafka/book-event" -Method POST -ContentType "application/json" -Body '{"eventType":"BOOK_ADDED","bookId":"custom-book-1","title":"Custom Book","author":"Custom Author"}'

# Send message to specific partition
Invoke-RestMethod -Uri "http://localhost:8081/api/kafka/partition/1?message=Hello from partition 1" -Method POST

# Send batch messages
Invoke-RestMethod -Uri "http://localhost:8081/api/kafka/batch/partition-demo?count=10" -Method POST
```

**cURL Examples:**
```bash
# Get Kafka information
curl -X GET http://localhost:8081/api/kafka/info

# Send custom book event
curl -X POST http://localhost:8081/api/kafka/book-event \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "BOOK_ADDED",
    "bookId": "custom-book-1",
    "title": "Custom Book",
    "author": "Custom Author"
  }'

# Send custom borrow event
curl -X POST http://localhost:8081/api/kafka/borrow-event \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "BOOK_BORROWED",
    "bookTitle": "Custom Book",
    "borrowCount": 10,
    "userId": "user123"
  }'

# Send message to specific partition
curl -X POST "http://localhost:8081/api/kafka/partition/1?message=Hello%20Partition%201"

# Send message with custom key
curl -X POST "http://localhost:8081/api/kafka/send-with-key?topic=partition-demo&key=my-key&message=Hello%20with%20key"

# Send batch messages
curl -X POST "http://localhost:8081/api/kafka/batch/partition-demo?count=10"
```

## Monitoring Kafka

### Using Kafka UI Dashboard (Recommended)

1. **Open Kafka UI**: http://localhost:8080
2. **Navigate to Topics**: View `book-events`, `borrow-events`, `partition-demo`
3. **Monitor Messages**: See real-time message flow
4. **Consumer Groups**: Monitor all 4 consumer groups
5. **Partition Distribution**: Observe message distribution across partitions

### Using Docker Commands

```bash
# View Kafka logs
docker-compose logs -f kafka

# Execute commands in Kafka container
docker exec -it kafka bash

# Inside Kafka container - List topics
kafka-topics --bootstrap-server localhost:9092 --list

# Describe a topic
kafka-topics --bootstrap-server localhost:9092 --describe --topic book-events

# List consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 --list

# Describe consumer group
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group library-group

# Console consumer (listen to messages)
kafka-console-consumer --bootstrap-server localhost:9092 --topic book-events --from-beginning
```

### Using Local Kafka Commands (if running locally)

```powershell
# List topics
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

# Describe topic
.\bin\windows\kafka-topics.bat --describe --topic book-events --bootstrap-server localhost:9092

# List consumer groups
.\bin\windows\kafka-consumer-groups.bat --bootstrap-server localhost:9092 --list

# Describe consumer group
.\bin\windows\kafka-consumer-groups.bat --bootstrap-server localhost:9092 --describe --group library-group

# Console consumer (for testing)
.\bin\windows\kafka-console-consumer.bat --topic book-events --from-beginning --bootstrap-server localhost:9092

# Listen to specific partition
.\bin\windows\kafka-console-consumer.bat --topic partition-demo --partition 1 --from-beginning --bootstrap-server localhost:9092
```

## Testing

Run the integration tests:
```bash
mvn test
```

The tests use embedded Kafka and don't require external Kafka setup.

## Key Concepts Demonstrated

### Producer Configuration
- **Acknowledgments**: `acks=all` ensures all replicas acknowledge
- **Retries**: Automatic retry on failures
- **Batching**: Groups messages for efficiency
- **Compression**: Optional data compression

### Consumer Configuration
- **Auto Offset Reset**: Start from earliest/latest messages
- **Manual Commit**: Explicit offset management
- **Max Poll Records**: Control batch size
- **Session Timeout**: Consumer heartbeat settings

### Partition Benefits
- **Scalability**: Parallel processing across partitions
- **Ordering**: Messages in same partition maintain order
- **Load Distribution**: Even distribution across consumers

### Consumer Group Benefits
- **Scalability**: Multiple consumers share load
- **Fault Tolerance**: Consumer failure handling
- **Independent Processing**: Different groups process independently

## Troubleshooting

### Docker Environment Issues

1. **Services not starting**:
   ```bash
   # Check service status
   docker-compose ps
   
   # Check logs for errors
   docker-compose logs kafka
   docker-compose logs app
   ```

2. **Port conflicts**:
   - If port 8080 is in use, Kafka UI won't start
   - If port 8081 is in use, the app won't start
   - Change ports in `docker-compose.yml` if needed

3. **Docker disk space**:
   ```bash
   # Clean up Docker resources
   docker system prune -a
   ```

4. **Fresh restart**:
   ```bash
   # Stop and remove everything
   docker-compose down -v
   
   # Start fresh
   docker-compose up --build
   ```

### Local Kafka Issues

1. **Connection Refused**: Ensure Kafka is running on localhost:9092
2. **Topic Not Found**: Create topics before running the application
3. **Consumer Lag**: Check consumer group status and partition assignment

### Health Checks

```bash
# Application health
curl http://localhost:8081/actuator/health

# Kafka topics and consumer info
curl http://localhost:8081/api/kafka/info

# Test basic functionality
curl -X POST http://localhost:8081/api/books -H "Content-Type: application/json" -d '{"title":"Test Book","author":"Test Author"}'
```

### Useful Commands

**Docker Environment:**
```bash
# Reset consumer group offset (from within Kafka container)
docker exec -it kafka kafka-consumer-groups --bootstrap-server localhost:9092 --group library-group --reset-offsets --to-earliest --topic book-events --execute

# Create additional topics
docker exec -it kafka kafka-topics --create --topic new-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

**Local Kafka:**
```powershell
# Reset consumer group offset
.\bin\windows\kafka-consumer-groups.bat --bootstrap-server localhost:9092 --group library-group --reset-offsets --to-earliest --topic book-events --execute

# Delete topic (if needed)
.\bin\windows\kafka-topics.bat --delete --topic book-events --bootstrap-server localhost:9092
```

## Architecture Overview

### Docker Compose Architecture

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
│  │            Spring Boot Application (:8081)             │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐ │  │
│  │  │  Producer   │  │  Consumers  │  │  REST API       │ │  │
│  │  │  Service    │  │  (Groups)   │  │  Controllers    │ │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────┘ │  │
│  └─────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Application Flow

```
┌─────────────────┐    ┌──────────────┐    ┌─────────────────┐
│   Library API   │───▶│    Kafka     │───▶│   Consumers     │
│   (Producer)    │    │   Brokers    │    │  (Multiple      │
│  /api/books/*   │    │              │    │   Groups)       │
└─────────────────┘    └──────────────┘    └─────────────────┘
        │                      │                      │
        │                      │                      ▼
        ▼                      ▼              ┌─────────────────┐
┌─────────────────┐    ┌──────────────┐    │   library-group │
│   Direct Kafka  │    │  Partitions  │    │  (Main Process) │
│   API Endpoints │    │   (0,1,2)    │    └─────────────────┘
│  /api/kafka/*   │    └──────────────┘              │
└─────────────────┘                                  ▼
                                               ┌─────────────────┐
                    Topics Created:            │ library-group-1 │
                    • book-events              │  (Analytics)    │
                    • borrow-events            └─────────────────┘
                    • partition-demo                   │
                                                       ▼
                                               ┌─────────────────┐
                                               │ library-group-2 │
                                               │ (Notifications) │
                                               └─────────────────┘
                                                       │
                                                       ▼
                                               ┌─────────────────┐
                                               │library-group-   │
                                               │record-listener  │
                                               │ (Inspection)    │
                                               └─────────────────┘
```

### Service Endpoints Summary

| Service | URL | Purpose |
|---------|-----|---------|
| Spring Boot App | http://localhost:8081 | Main application and API |
| Kafka UI | http://localhost:8080 | Kafka management interface |
| Health Check | http://localhost:8081/actuator/health | Application health |
| Kafka Info | http://localhost:8081/api/kafka/info | Kafka cluster information |

### Profile Configuration

The application supports profile-based configuration:

- **`with-kafka`**: Full Kafka integration (used in Docker)
- **`no-kafka`**: Kafka disabled (for testing without message broker)

This setup demonstrates production-ready Kafka patterns including error handling, monitoring, scalable consumer groups, and containerized deployment.
