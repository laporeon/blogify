package com.laporeon.posts_api.services;

import com.laporeon.posts_api.dto.request.PostRequestDTO;
import com.laporeon.posts_api.dto.response.PageResponseDTO;
import com.laporeon.posts_api.dto.response.PostResponseDTO;
import com.laporeon.posts_api.entities.Post;
import com.laporeon.posts_api.exceptions.PostNotFoundException;
import com.laporeon.posts_api.mappers.PostMapper;
import com.laporeon.posts_api.repositories.PostRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService Tests")
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private static final String VALID_TITLE = "Getting Started with Spring Boot";
    private static final String VALID_DESCRIPTION = "A comprehensive guide to building REST APIs with Spring Boot framework.";
    private static final String VALID_BODY = "Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications.";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    private Post mockedPostEntity;
    private PostResponseDTO mockedPostResponse;
    private Instant createdAt;
    private Instant updatedAt;

    @BeforeEach
    void setUp() {
        createdAt = Instant.now().minus(1, ChronoUnit.DAYS);
        updatedAt = Instant.now();

        mockedPostEntity = Post.builder()
                               .id(new ObjectId().toString())
                               .title(VALID_TITLE)
                               .description(VALID_DESCRIPTION)
                               .body(VALID_BODY)
                               .createdAt(createdAt)
                               .updatedAt(updatedAt)
                               .build();

        mockedPostResponse = new PostResponseDTO(
                mockedPostEntity.getId(),
                mockedPostEntity.getTitle(),
                mockedPostEntity.getDescription(),
                mockedPostEntity.getBody(),
                mockedPostEntity.getCreatedAt(),
                mockedPostEntity.getUpdatedAt()
        );
    }

    @Test
    @DisplayName("Should save Post successfully when given valid request data")
    void shouldSavePostSuccessfullyWhenGivenRequestData() {
        PostRequestDTO requestDTO = new PostRequestDTO(VALID_TITLE, VALID_DESCRIPTION, VALID_BODY);

        when(postMapper.toEntity(any(PostRequestDTO.class))).thenReturn(mockedPostEntity);
        when(postRepository.save(any(Post.class))).thenReturn(mockedPostEntity);
        when(postMapper.toResponseDTO(any(Post.class))).thenReturn(mockedPostResponse);

        PostResponseDTO response = postService.create(requestDTO);

        assertThat(response.id()).isEqualTo(mockedPostResponse.id());
        assertThat(response.title()).isEqualTo(mockedPostResponse.title());
        assertThat(response.createdAt()).isNotNull();

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("Should return page of posts when given valid pageable")
    void shouldReturnPostsPageWhenGivenValidPageable() {
        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE);

        List<Post> mockedPostsList = List.of(mockedPostEntity);
        Page<Post> expectedPage = new PageImpl<>(mockedPostsList, pageable, mockedPostsList.size());

        PageResponseDTO<PostResponseDTO> mockedPageResponse = new PageResponseDTO<>(
                expectedPage.getContent()
                            .stream()
                            .map(postMapper::toResponseDTO)
                            .toList(),
                expectedPage.getNumber(),
                expectedPage.getSize(),
                expectedPage.getTotalPages(),
                expectedPage.getTotalElements(),
                expectedPage.getNumberOfElements(),
                expectedPage.getSort().isSorted(),
                expectedPage.isFirst(),
                expectedPage.isEmpty(),
                expectedPage.isLast(),
                expectedPage.hasNext()
        );

        when(postRepository.findAll(pageable)).thenReturn(expectedPage);
        when(postMapper.toPageResponseDTO(any(Page.class))).thenReturn(mockedPageResponse);

        PageResponseDTO<PostResponseDTO> result = postService.listPosts(pageable, null, null);

        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.content()).hasSize(1);

        verify(postRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return empty page when no posts exist")
    void shouldReturnEmptyPageWhenNoPostsExist() {
        Pageable pageable = PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE);
        Page<Post> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        PageResponseDTO<PostResponseDTO> emptyResponse = new PageResponseDTO<>(
                emptyPage.getContent().stream().map(postMapper::toResponseDTO).toList(),
                emptyPage.getNumber(),
                emptyPage.getSize(),
                emptyPage.getTotalPages(),
                emptyPage.getTotalElements(),
                emptyPage.getNumberOfElements(),
                emptyPage.getSort().isSorted(),
                emptyPage.isFirst(),
                emptyPage.isEmpty(),
                emptyPage.isLast(),
                emptyPage.hasNext()
        );

        when(postRepository.findAll(pageable)).thenReturn(emptyPage);
        when(postMapper.toPageResponseDTO(any(Page.class))).thenReturn(emptyResponse);

        PageResponseDTO<PostResponseDTO> result = postService.listPosts(pageable, null, null);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
        assertThat(result.isEmpty()).isTrue();

        verify(postRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return post when given existing id")
    void shouldReturnPostWhenGivenExistingId() {
        when(postRepository.findById(mockedPostEntity.getId())).thenReturn(Optional.of(mockedPostEntity));
        when(postMapper.toResponseDTO(any(Post.class))).thenReturn(mockedPostResponse);

        PostResponseDTO sut = postService.findById(mockedPostEntity.getId());

        assertThat(sut.id()).isEqualTo(mockedPostResponse.id());
        assertThat(sut.title()).isEqualTo(mockedPostResponse.title());

        verify(postRepository, times(1)).findById(mockedPostEntity.getId());
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when id does not exist")
    void shouldThrowPostNotFoundExceptionWhenIdDoesNotExist() {
        String invalidId = "68e0234a70424186e056e45f";

        when(postRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findById(invalidId));

        verify(postRepository, times(1)).findById(invalidId);
    }


    @Test
    @DisplayName("Should successfully update post when given existing id and valid request data")
    void shouldUpdatePostWhenGivenExistingIdAndValidRequestData() {
        PostRequestDTO requestDTO = new PostRequestDTO(null, VALID_DESCRIPTION, null);

        when(postRepository.findById(mockedPostEntity.getId())).thenReturn(Optional.of(mockedPostEntity));
        when(postMapper.toResponseDTO(any(Post.class))).thenReturn(mockedPostResponse);
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        PostResponseDTO response = postService.update(mockedPostEntity.getId(), requestDTO);

        assertThat(response.id()).isEqualTo(mockedPostResponse.id());
        assertThat(response.title()).isEqualTo(mockedPostResponse.title());
        assertThat(response.createdAt()).isEqualTo(mockedPostResponse.createdAt());
        assertThat(response.updatedAt()).isNotNull();

        verify(postRepository, times(1)).findById(mockedPostEntity.getId());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when updating post with non existing id")
    void shouldThrowPostNotFoundExceptionWhenUpdatingPostWithNonExistingId() {
        String invalidId = "68e0234a70424186e056e45f";
        PostRequestDTO requestDTO = new PostRequestDTO(VALID_TITLE, VALID_DESCRIPTION, VALID_BODY);

        when(postRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.update(invalidId, requestDTO));

        verify(postRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("Should delete post when given existing id")
    void shouldDeletePostWhenGivenExistingId() {
        when(postRepository.findById(mockedPostEntity.getId())).thenReturn(Optional.of(mockedPostEntity));

        doNothing().when(postRepository).deleteById(mockedPostEntity.getId());

        postService.delete(mockedPostEntity.getId());

        verify(postRepository, times(1)).findById(mockedPostEntity.getId());
        verify(postRepository, times(1)).deleteById(mockedPostEntity.getId());
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when deleting post with non existing id")
    void shouldThrowPostNotFoundExceptionWhenDeletingPostWithNonExistingId() {
        String invalidId = "68e0234a70424186e056e45f";

        when(postRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.delete(invalidId));

        verify(postRepository, times(1)).findById(invalidId);
        verify(postRepository, never()).deleteById(invalidId);
    }
}
