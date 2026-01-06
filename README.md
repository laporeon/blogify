<h1 align="center"> Posts API

![java](https://img.shields.io/static/v1?label=java&message=21.0.8&color=2d3748&logo=openjdk&style=flat-square)
![spring boot](https://img.shields.io/static/v1?label=spring%20boot&message=3.5.4&color=2d3748&logo=springboot&style=flat-square)
![mongodb](https://img.shields.io/badge/mongodb-8.0.17-4b32c3?style=flat-square&logo=mongodb&color=2d3748)
![docker](https://img.shields.io/static/v1?label=docker&message=28.5.0&color=2d3748&logo=docker&style=flat-square)

</h1>

## Table of Contents

- [About](#about)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
    - [Configuring](#configuring)
      - [.env](#env)
- [Usage](#usage)
    - [Starting](#starting)
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

## Requirements:

**For Docker (Recommended):**

- Docker & Docker Compose

**For Local Development:**

- Java 21+
- Maven 3.9+
- MongoDB

## Getting Started

### Configuring

#### **.env**

Using Docker, `.env` file is optional since default values exist in [Docker Compose file](./docker-compose.yml). For
local development **without Docker**, you must set `MONGO_USER` and `MONGO_PASSWORD` environment variables to
connect to your MongoDB instance properly. Other variables have sensible fallback defaults in the application
configuration.

Rename  `.env.example` to `.env` and modify variables according to your needs.

| Variable          | For Docker                       | For Local Development       | Description         |
|-------------------|----------------------------------|-----------------------------|---------------------|
| PORT              | Optional (Default: "8080")       | Optional (Default: "8080")  | Server port         |
| MONGO_USER     | Optional (Default: "trademap")   | **Required**                | MongoDB username |
| MONGO_PASSWORD | Optional (Default: "dbpassword") | **Required**                | MongoDB password |
| MONGO_DATABASE       | Optional (Default: "posts")      | Optional (Default: "posts") | MongoDB database |

## Usage

### **Starting**

For the fastest setup, it is recommended to use Docker Compose to start the app and its services:

```bash
# Run docker compose command to start all services
$ docker compose up -d --build
```

Access the application at `http://localhost:8080/api/v1/posts` (or the port you configured).

### **Routes**

| Route        | HTTP Method | Params                                                                                                                                                                                                                                                                                                                                               | Description                              | Auth Method |
|--------------|-------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------|-------------|
| `/docs`      | GET         | -                                                                                                                                                                                                                                                                                                                                                    | Swagger documentation                    | None        |
| `/api/v1/posts` | POST        | Body with `title`, `description` and `body`.                                                                                                                                                                                                                                                                                                         | Create a new post                        | None        |
| `/api/v1/posts` | GET         | **Query Parameters:**<br>• `page` - Page number (default: 0)<br>• `size` - Page size (default: 10)<br>• `orderBy` - Sort field (default: "name")<br>• `direction` - Sort direction: ASC/DESC (default: "ASC")<br>• `startDate` - Filter posts from this date (format: yyyy-MM-dd)<br>• `endDate` - Filter posts until this date (format: yyyy-MM-dd) | Retrieve paginated posts with sorting    | None        |
| `/api/v1/posts/:id` | GET         | `:id`                                                                                                                                                                                                                                                                                                                                                | Retrieve existing post by its unique id. | None        |
| `/api/v1/posts/:id` | PUT         | `:id` + Body with fields to be updated (title, body and/or description)                                                                                                                                                                                                                                                                              | Update post information                  | None        |
| `/api/v1/posts/:id` | DELETE      | `:id`                                                                                                                                                                                                                                                                                                                                                | Delete an existing post.                 | None        |

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

- `PUT /api/v1/posts/:id`

Request body:

```json
{
  "title": "Getting Started with Spring Boot 4"
}
```

[⬆ Back to the top](#-posts-api)
