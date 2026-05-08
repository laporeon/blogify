package com.laporeon.blogify.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequestDTO(
        @NotBlank(message = "Title is required.")
        @Size(min = 10, max = 100, message = "Title must be between {min} and {max} characters long.")
        @Schema(example = "Getting Started with Spring Boot")
        String title,
        @NotBlank(message = "Description is required.")
        @Size(min = 20, max = 150, message = "Description must be between {min} and {max} characters long.")
        @Schema(example = "A comprehensive guide to building REST APIs with Spring Boot framework.")
        String description,
        @NotBlank(message = "Body content is required.")
        @Size(min = 60, max = 500, message = "Body content must be between {min} and {max} characters long.")
        @Schema(example = "Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications.")
        String body
) {}