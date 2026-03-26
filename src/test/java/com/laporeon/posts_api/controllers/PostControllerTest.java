package com.laporeon.posts_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laporeon.posts_api.dto.request.PostRequestDTO;
import com.laporeon.posts_api.dto.request.PostUpdateDTO;
import com.laporeon.posts_api.dto.response.PageResponseDTO;
import com.laporeon.posts_api.dto.response.PostResponseDTO;
import com.laporeon.posts_api.exceptions.custom.PostNotFoundException;
import com.laporeon.posts_api.services.PostService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@DisplayName("PostController Tests")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    private static final String VALID_TITLE = "Getting Started with Spring Boot";
    private static final String VALID_DESCRIPTION = "A comprehensive guide to building REST APIs with Spring Boot framework.";
    private static final String VALID_BODY = "Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications.";
    private static final String VALIDATION_ERROR_MESSAGE = "Request validation failed for one or more fields";
    private static final String NOT_FOUND_MESSAGE = "Post with id %s not found.";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final String POSTS_ENDPOINT = "/api/v1/posts";

    private PostResponseDTO mockedPostResponse;
    private String validPostId;

    @BeforeEach
    void setUp() {
        validPostId = new ObjectId().toString();
        Instant createdAt = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant updatedAt = Instant.now();

        mockedPostResponse = new PostResponseDTO(
                validPostId,
                VALID_TITLE,
                VALID_DESCRIPTION,
                VALID_BODY,
                createdAt,
                updatedAt
        );
    }

    @Test
    @DisplayName("POST /api/v1/posts - Should return 201 when given valid request data")
    void shouldReturnCreatedWhenGivenValidRequestData() throws Exception {
        PostRequestDTO validRequest = new PostRequestDTO(VALID_TITLE, VALID_DESCRIPTION, VALID_BODY);

        when(postService.create(any(PostRequestDTO.class))).thenReturn(mockedPostResponse);

        mockMvc.perform(post(POSTS_ENDPOINT)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(validRequest)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(mockedPostResponse.id()))
               .andExpect(jsonPath("$.title").value(mockedPostResponse.title()));
    }


    @Test
    @DisplayName("POST /api/v1/posts - Should return 400 when required fields are missing")
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        PostRequestDTO invalidRequest = new PostRequestDTO(null, null, null);

        mockMvc.perform(post(POSTS_ENDPOINT)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value(VALIDATION_ERROR_MESSAGE))
               .andExpect(jsonPath("$.errors").isArray())
               .andExpect(jsonPath("$.errors[0].field").value("body"))
               .andExpect(jsonPath("$.errors[0].message").value("Body content is required."))
               .andExpect(jsonPath("$.errors[1].field").value("description"))
               .andExpect(jsonPath("$.errors[1].message").value("Description is required."))
               .andExpect(jsonPath("$.errors[2].field").value("title"))
               .andExpect(jsonPath("$.errors[2].message").value("Title is required."));
    }

    @Test
    @DisplayName("POST /api/v1/posts - Should return 400 when given invalid required fields")
    void shouldReturn400WhenGivenInvalidRequiredFields() throws Exception {
        PostRequestDTO invalidRequest = new PostRequestDTO("tit", "desc", "body");

        mockMvc.perform(post(POSTS_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value(VALIDATION_ERROR_MESSAGE))
               .andExpect(jsonPath("$.errors").isArray())
               .andExpect(jsonPath("$.errors[0].field").value("body"))
               .andExpect(jsonPath("$.errors[0].message").value("Body content must be between 60 and 500 characters long."))
               .andExpect(jsonPath("$.errors[1].field").value("description"))
               .andExpect(jsonPath("$.errors[1].message").value("Description must be between 20 and 150 characters long."))
               .andExpect(jsonPath("$.errors[2].field").value("title"))
               .andExpect(jsonPath("$.errors[2].message").value("Title must be between 10 and 100 characters long."));
    }

    @Test
    @DisplayName("GET /api/v1/posts - Should return 200 and paginated posts")
    void shouldReturn200AndPaginatedPosts() throws Exception {
        List<PostResponseDTO> content = List.of(mockedPostResponse);
        PageResponseDTO<PostResponseDTO> pageResponse = new PageResponseDTO<>(
                content,
                DEFAULT_PAGE,
                DEFAULT_SIZE,
                1,
                1,
                1,
                true,
                true,
                false,
                true,
                false
        );

        when(postService.listPosts(any(Pageable.class), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get(POSTS_ENDPOINT)
                                .param("page", String.valueOf(DEFAULT_PAGE))
                                .param("size", String.valueOf(DEFAULT_SIZE)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content").isArray())
               .andExpect(jsonPath("$.content").isNotEmpty())
               .andExpect(jsonPath("$.totalElements").value(1))
               .andExpect(jsonPath("$.pageNumber").value(DEFAULT_PAGE))
               .andExpect(jsonPath("$.pageSize").value(DEFAULT_SIZE));
    }

    @Test
    @DisplayName("GET /api/v1/posts - Should return 200 with empty content when no posts exist")
    void shouldReturn200WithEmptyContentWhenNoPostsExist() throws Exception {
        PageResponseDTO<PostResponseDTO> emptyPage = new PageResponseDTO<>(
                List.of(),
                0,
                0,
                0,
                0,
                0,
                false,
                false,
                true,
                true,
                false
        );

        when(postService.listPosts(any(Pageable.class), any(), any())).thenReturn(emptyPage);

        mockMvc.perform(get(POSTS_ENDPOINT))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content").isEmpty())
               .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/posts/{id} - Should return 200 when id exists")
    void shouldReturn200WhenIdExists() throws Exception {
        when(postService.findById(validPostId)).thenReturn(mockedPostResponse);

        mockMvc.perform(get(POSTS_ENDPOINT + "/" + validPostId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(mockedPostResponse.id()))
               .andExpect(jsonPath("$.title").value(mockedPostResponse.title()));
    }

    @Test
    @DisplayName("GET /api/v1/posts/{id} - Should return 404 when id does not exists")
    void shouldReturn404WhenIdDoesNotExists() throws Exception {
        String invalidId = "68e0124a70424186e056e45d";

        when(postService.findById(invalidId)).thenThrow(new PostNotFoundException(invalidId));

        mockMvc.perform(get(POSTS_ENDPOINT + "/" + invalidId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE.formatted(invalidId)));
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{id} - Should return 200 when updating post with existing id and valid request data")
    void shouldReturn200WhenUpdatingPostWithExistingIdAndValidRequestData() throws Exception {
        PostUpdateDTO validRequest = new PostUpdateDTO(VALID_TITLE, null, null);

        when(postService.update(eq(validPostId), any(PostUpdateDTO.class)))
                .thenReturn(mockedPostResponse);

        mockMvc.perform(put(POSTS_ENDPOINT + "/" + validPostId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(validRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(mockedPostResponse.id()))
               .andExpect(jsonPath("$.title").value(mockedPostResponse.title()))
               .andExpect(jsonPath("$.description").value(mockedPostResponse.description()))
               .andExpect(jsonPath("$.body").value(mockedPostResponse.body()));
    }

    @Test
    @DisplayName("PUT /api/v1/posts/{id} - Should return 404 when updating post with non existing id")
    void shouldReturn404WhenGivenNonExistingId() throws Exception {
        String invalidId = "68e0124a70424186e056e45d";
        PostUpdateDTO validRequest = new PostUpdateDTO(VALID_TITLE, null, null);

        doThrow(new PostNotFoundException(invalidId))
                .when(postService)
                .update(invalidId, validRequest);

        mockMvc.perform(put(POSTS_ENDPOINT + "/" + invalidId)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(validRequest)))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE.formatted(invalidId)));
    }

    @Test
    @DisplayName("DELETE /api/v1/posts/{id} - Should return 204 when deleting post with existing id")
    void shouldReturn204WhenDeletingPostWithExistingId() throws Exception {
        doNothing().when(postService).delete(validPostId);

        mockMvc.perform(delete(POSTS_ENDPOINT + "/" + validPostId))
               .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/posts/{id} - Should return 404 when deleting post with non existing id")
    void shouldReturn404WhenDeletingPostWithNonExistingId() throws Exception {
        String invalidId = "68e0124a70424186e056e45d";

        doThrow(new PostNotFoundException(invalidId))
                .when(postService)
                .delete(invalidId);

        mockMvc.perform(delete(POSTS_ENDPOINT + "/" + invalidId))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE.formatted(invalidId)));
    }
}