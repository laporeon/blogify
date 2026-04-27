package com.laporeon.splog.utils;

public class SwaggerExamples {

    public static final String CREATE_POST_SUCCESS_RESPONSE = """
            {
                "id": "507f1f77bcf86cd799439011",
                "title": "Getting Started with Spring Boot",
                "description": "A comprehensive guide to building REST APIs with Spring Boot framework.",
                "body": "Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications.",
                "createdAt": "2025-10-03T21:17:35.960908672Z",
                "updatedAt": "2025-10-03T21:17:35.960908672Z"
            }
            """;

    public static final String LIST_POSTS_PAGE_RESPONSE = """
            {
                "content": [
                    {
                    "id": "507f1f77bcf86cd799439011",
                    "title": "Getting Started with Spring Boot",
                    "description": "A comprehensive guide to building REST APIs with Spring Boot framework.",
                    "body": "Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can just run. In this post, we'll explore the fundamentals of creating a REST API...",
                    "createdAt": "2025-10-03T21:17:35.960908672Z",
                    "updatedAt": "2025-10-03T21:17:35.960908672Z"
                    },
                    {
                    "id": "507f1f77bcf86cd799439012",
                    "title": "MongoDB with Spring Data",
                    "description": "Learn how to integrate MongoDB with Spring Data for seamless database operations.",
                    "body": "MongoDB is a popular NoSQL database that works perfectly with Spring Boot applications. In this tutorial, we'll set up MongoDB integration using Spring Data MongoDB...",
                    "createdAt": "2025-10-11T10:37:35.960908672Z",
                    "updatedAt": "2025-10-11T10:37:35.960908672Z"
                    },
                    {
                    "id": "507f1f77bcf86cd799439013",
                    "title": "API Documentation with Swagger",
                    "description": "Best practices for documenting REST APIs using OpenAPI and Swagger UI.",
                    "body": "Good API documentation is crucial for developer experience. Swagger UI provides an interactive interface for testing and understanding your APIs. Here's how to implement it properly...",
                    "createdAt": "2025-10-28T18:37:35.960908672Z",
                    "updatedAt": "2025-10-28T18:37:35.960908672Z"
                    }
                ],
                "pageNumber": 0,
                "pageSize": 10,
                "totalPages": 1,
                "totalElements": 3,
                "numberOfElements": 3,
                "isFirstPage": true,
                "isLastPage": true,
                "isEmpty": false,
                "isSorted": false,
                "isUnsorted": true
            }
            """;

    public static final String VALIDATION_ERROR_RESPONSE = """
            {
              "status": 400,
              "error": "Validation Error",
              "message": "Request validation failed for one or more fields",
              "errors": [
                {
                  "message": "Body content must be between 60 and 500 characters long.",
                  "field": "body"
                },
                {
                  "message": "Description must be between 20 and 150 characters long.",
                  "field": "description"
                },
                {
                  "message": "Title must be between 10 and 100 characters long.",
                  "field": "title"
                }
              ],
              "timestamp": "2025-12-17T17:03:01.851260389Z"
            }
            """;

    public static final String NOT_FOUND_ERROR = """
            {
              "status": 404,
              "message": "Post with id 6942e984836f586fa47e52ff not found.",
              "timestamp": "2025-12-17T17:34:36.964178379Z"
            }
            """;

    public static final String INTERNAL_SERVER_ERROR = """
            {
              "status": 500,
              "message": "An unexpected error occurred",
              "timestamp": "2025-12-10T18:08:56.353210281Z"
            }
            """;
}
