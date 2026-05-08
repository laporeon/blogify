package com.laporeon.blogify.controllers;

import com.laporeon.blogify.dto.request.PostRequestDTO;
import com.laporeon.blogify.dto.request.PostUpdateDTO;
import com.laporeon.blogify.dto.response.ErrorResponseDTO;
import com.laporeon.blogify.dto.response.PageResponseDTO;
import com.laporeon.blogify.dto.response.PostResponseDTO;
import com.laporeon.blogify.services.PostService;
import com.laporeon.blogify.utils.SwaggerExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@Tag(name = "Posts", description = "Endpoints for managing blog posts")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Create a new post",
            description = "Creates a new post with specified title, description and body content. Validates input and returns saved post.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Post successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.CREATE_POST_SUCCESS_RESPONSE))),
                    @ApiResponse(responseCode = "400", description = "Request validation failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.VALIDATION_ERROR_RESPONSE))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)))
            }
    )
    @PostMapping
    public ResponseEntity<PostResponseDTO> create(@Valid @RequestBody PostRequestDTO dto) {
        PostResponseDTO postResponseDTO = postService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDTO);
    }

    @Operation(
            summary = "List all posts",
            description = "Returns a paginated and sorted list of posts, allowing control over page number, size, order by field, sort direction and range date.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Posts page successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.LIST_POSTS_PAGE_RESPONSE))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)))
            }
    )
    @GetMapping
    public ResponseEntity<PageResponseDTO> listPosts(@Parameter(description = "Page number")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "Entity field used for sorting",
                    schema = @Schema(allowableValues = {"id", "title", "description", "body", "createdAt", "updatedAt"}),
                    example = "title")
            @RequestParam(value = "orderBy", defaultValue = "title") String orderBy,
            @Parameter(description = "Sort direction",
                    schema = @Schema(allowableValues = {"ASC", "DESC"}),
                    example = "ASC")
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @Parameter(description = "Filter posts from this date (format: yyyy-MM-dd)")
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @Parameter(description = "Filter posts until this date (format: yyyy-MM-dd)")
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), orderBy));

        PageResponseDTO<PostResponseDTO> posts = postService.listPosts(pageable, startDate, endDate);
        return ResponseEntity.ok().body(posts);
    }

    @Operation(
            summary = "Get post by ID",
            description = "Fetches a post by its unique ID. Returns 404 error if post does not exist.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.LIST_POSTS_PAGE_RESPONSE))),
                    @ApiResponse(responseCode = "404", description = "Resource not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.NOT_FOUND_ERROR))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> findPostById(@PathVariable("id") String id) {
        PostResponseDTO postResponseDTO = postService.findById(id);
        return ResponseEntity.ok().body(postResponseDTO);
    }

    @Operation(
            summary = "Update existing post",
            description = "Updates the title, description, or content of an existing post identified by ID. Validates input and handles 404 if ID not found.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.CREATE_POST_SUCCESS_RESPONSE))),
                    @ApiResponse(responseCode = "400", description = "Request validation failed for one or more fields",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.VALIDATION_ERROR_RESPONSE))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)))
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> update(
            @PathVariable("id") String id,
            @Valid @RequestBody PostUpdateDTO postUpdateDTO) {
        PostResponseDTO postResponseDTO = postService.update(id, postUpdateDTO);
        return ResponseEntity.ok().body(postResponseDTO);
    }

    @Operation(
            summary = "Delete post",
            description = "Deletes a post based on its unique ID. Returns 404 if post not found.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Post successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Resource not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.NOT_FOUND_ERROR))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(value = SwaggerExamples.INTERNAL_SERVER_ERROR)))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
