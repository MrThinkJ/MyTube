# MyTube

MyTube is a microservices-based video sharing platform inspired by YouTube, built with Spring Boot and modern technologies.

## Architecture

The application follows a microservices architecture with the following components:

- **API Gateway (Port 9191)**: Entry point for all client requests
- **Config Service (Port 8888)**: Centralized configuration management
- **Eureka Server (Port 8761)**: Service discovery and registration
- **Core Services**:
  - Video Service (Port 8081)
  - Video Processing Service (Port 8082)
  - User Service (Port 8083)
  - Security Service (Port 8084)
  - Comment Service (Port 8085)
  - Notification Service (Port 8086)
  - Search Service (Port 8087)

## Technologies

- **Spring Boot**: Framework for building microservices
- **Spring Cloud**: Tools for common distributed system patterns
- **Minio**: Object storage for video files
- **ElasticSearch**: Powering the search functionality
- **Docker/Docker Compose**: Containerization and orchestration
- **PostgreSQL**: Relational database for persistent storage

## Getting Started

### Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven

### Setup and Running

1. Clone the repository:

   ```
   git clone https://github.com/yourusername/MyTube.git
   cd MyTube
   ```

2. Start the infrastructure services with Docker Compose:

   ```
   docker-compose up -d
   ```

3. Start the microservices in the following order:

   - Config Service
   - Eureka Server
   - Core Services (User, Video, etc.)
   - API Gateway

   Each service can be started from its directory with:

   ```
   cd service-name
   mvn spring-boot:run
   ```

4. Access the application at `http://localhost:9191`

## Service Overview

- **Video Service**: Manages video metadata, uploads, and retrievals
- **Video Processing Service**: Handles video transcoding and processing
- **User Service**: User account management
- **Security Service**: Authentication and authorization
- **Comment Service**: Video comments functionality
- **Notification Service**: User notifications
- **Search Service**: Video search functionality with ElasticSearch

## Architecture Diagram

```
                   ┌─────────────┐
                   │  Client App │
                   └──────┬──────┘
                          │
                          ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│Config Service│◄───┤ API Gateway ├───►│Eureka Server│
└─────────────┘    └──────┬──────┘    └─────────────┘
                          │
          ┌───────────────┼───────────────┐
          │               │               │
          ▼               ▼               ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ User Service │  │ Video Service│  │Comment Service│
└──────────────┘  └──────────────┘  └──────────────┘
          │               │               │
          │               │               │
          ▼               ▼               ▼
┌──────────────┐  ┌────────────────┐ ┌─────────────┐
│Security      │  │Video Processing│ │Notification  │
│Service       │  │Service         │ │Service       │
└──────────────┘  └────────────────┘ └─────────────┘
                          │
                          ▼
                  ┌──────────────┐
                  │Search Service│
                  └──────────────┘
```

## Development

### Port Configuration

Service ports are defined in `ports.txt`:

```
api-gateway=9191
elastic-search=9200
config-service=8888
eureka-server=8761
search-service=8087
notification-service=8086
comment-service=8085
security-service=8084
user-service=8083
video-processing-service=8082
video-service=8081
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
