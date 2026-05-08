<h1 align="center"> Blogify

![java](https://img.shields.io/static/v1?label=java&message=21.0.8&color=2d3748&logo=openjdk&style=flat-square)
![spring boot](https://img.shields.io/static/v1?label=spring%20boot&message=4.0.6&color=2d3748&logo=springboot&style=flat-square)
![mongodb](https://img.shields.io/badge/mongodb-8.0.17-4b32c3?style=flat-square&logo=mongodb&color=2d3748)
![docker](https://img.shields.io/static/v1?label=docker&message=29.4.3&color=2d3748&logo=docker&style=flat-square)
![swagger](https://img.shields.io/static/v1?label=swagger&message=3.0.3&color=2d3748&logo=swagger&style=flat-square)
</h1>

## Table of Contents

- [About](#about)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
    - [Running with Docker (Recommended)](#running-with-docker-recommended)
    - [Running Locally (Without Docker)](#running-locally-without-docker)
    - [Environment Variables Reference](#environment-variables-reference)
- [Usage](#usage)
    - [Routes](#routes)
        - [Requests](#requests)

## About

This is my solution for the [TradeMap Code backend challenge](https://github.com/TradeMap-Code/desafio-backend). It's a
comprehensive REST API that simulates a backend service for a personal blog platform.

**Key features:**

- Input validation for post request payloads.
- Full CRUD operations for posts.
- Pagination support with flexible sorting.
- Swagger documentation for all endpoints.
- One-command deployment with Docker Compose
- Environment-based configuration via Spring Profiles (`dev` and `prod`)

## Requirements:

**For Docker (Recommended):**

- Docker & Docker Compose

**For Local Development:**

- Java 21+
- Maven 3.9+
- MongoDB

## Getting Started

### Running with Docker (Recommended)

This is the simplest setup. Docker Compose will automatically build and start all required services, using default values in the [Compose file](./docker-compose.yml).

```bash
docker compose up -d --build
```

Then access the application at `http://localhost:8080/` (or the port you configured).

**Optionally**, you can override any environment variables with your own settings.
```bash
cp .env.example .env
# Edit .env with your local values
```

### Running Locally (Without Docker)

1. Set the required environment variables using **either** method below:
    - **Option 1: Using `.env` file**. Copy the example file and edit with your values:
      ```bash
      cp .env.example .env
      # Edit .env with your local settings
      ```

    - **Option 2: Exporting via shell**
      ```bash
      export MONGO_USER=<your-mongodb-user-here>
      export MONGO_PASSWORD=<your-mongodb-password-here>
      export MONGO_DATABASE=<database-name>
      ```

2. (Optional) Set server port:
```bash
export PORT=8081
```
3. Start the application:
```bash
mvn spring-boot:run
```
4. Access at `http://localhost:8080/` (or the port you configured).


### Environment Variables Reference

> [!NOTE]
> The application loads MongoDB credentials based on the active Spring profile:
> - `dev`: Uses individual MongoDB credentials
> - `prod`: Uses a full MongoDB connection URI

| Variable               | For Docker                       | For Local Development          | For Production               | Description            |
|------------------------|----------------------------------|--------------------------------|------------------------------|------------------------|
| PORT                   | Optional (Default: "8080")       | Optional (Default: "8080")     | Auto-set by platform         | Server port            |
| SPRING_PROFILES_ACTIVE | Default: "dev"                   | Optional (Default: "dev")      | **Required** (set to "prod") | Active Spring profile  |
| MONGO_USER             | Optional (Default: "admin")      | **Required**                   | —                            | MongoDB username       |
| MONGO_PASSWORD         | Optional (Default: "dbpassword") | **Required**                   | —                            | MongoDB password       |
| MONGO_DATABASE         | Optional (Default: "blogifydb")   | Optional (Default: "blogifydb") | —                            | MongoDB database name  |
| MONGO_URI              | —                                | —                              | **Required**                 | MongoDB connection URI |

## Usage

Once the application is running, you can interact via Swagger UI at `/docs` or directly through HTTP requests.

### **Routes**

| Route                | HTTP Method | Params                                                                                                                                                                                                                                                                                                                                             | Description                              | Auth Method |
|----------------------|-------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------|-------------|
| `/docs`              | GET         | -                                                                                                                                                                                                                                                                                                                                                  | Swagger documentation                    | None        |
| `/api/v1/posts`      | POST        | Body with `title`, `description` and `body`.                                                                                                                                                                                                                                                                                                       | Create a new post                        | None        |
| `/api/v1/posts`      | GET         | **Query Parameters:**<br>• `page` - Page number (default: 0)<br>• `size` - Page size (default: 10)<br>• `orderBy` - Sort field (default: "title")<br>• `direction` - Sort direction: ASC/DESC (default: "ASC")<br>• `startDate` - Filter posts from this date (format: yyyy-MM-dd)<br>• `endDate` - Filter posts until this date (format: yyyy-MM-dd) | Retrieve paginated posts with sorting    | None        |
| `/api/v1/posts/{id}` | GET         | `{id}`                                                                                                                                                                                                                                                                                                                                             | Retrieve existing post by its unique id. | None        |
| `/api/v1/posts/{id}` | PUT         | `{id}` + Body with fields to be updated (title, body and/or description)                                                                                                                                                                                                                                                                           | Update post information                  | None        |
| `/api/v1/posts/{id}` | DELETE      | `{id}`                                                                                                                                                                                                                                                                                                                                             | Delete an existing post.                 | None        |

#### Requests

- `POST /api/v1/posts`

Request body:

```json
{
  "title": "Getting Started with Spring Boot",
  "description": "A comprehensive guide to building REST APIs with Spring Boot framework.",
  "body": "Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications."
}
```

- `PUT /api/v1/posts/{id}`

Request body:

```json
{
  "title": "Getting Started with Spring Boot 4"
}
```

[⬆ Back to the top](#-blogify)
