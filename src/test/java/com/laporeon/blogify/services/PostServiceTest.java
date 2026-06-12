package com.laporeon.blogify.services;

import com.laporeon.blogify.dto.request.PostRequestDTO;
import com.laporeon.blogify.dto.request.PostUpdateDTO;
import com.laporeon.blogify.dto.response.PageResponseDTO;
import com.laporeon.blogify.dto.response.PostResponseDTO;
import com.laporeon.blogify.entities.Post;
import com.laporeon.blogify.exceptions.custom.InvalidArgumentException;
import com.laporeon.blogify.exceptions.custom.PostNotFoundException;
import com.laporeon.blogify.mappers.PostMapper;
import com.laporeon.blogify.repositories.PostRepository;
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

    @BeforeEach
    void setUp() {
        Instant createdAt = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant updatedAt = Instant.now();

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
        PostRequestDTO validRequest = new PostRequestDTO(VALID_TITLE, VALID_DESCRIPTION, VALID_BODY);

        when(postMapper.toEntity(any(PostRequestDTO.class))).thenReturn(mockedPostEntity);
        when(postRepository.save(any(Post.class))).thenReturn(mockedPostEntity);
        when(postMapper.toResponseDTO(any(Post.class))).thenReturn(mockedPostResponse);

        PostResponseDTO response = postService.create(validRequest);

        assertThat(response.id()).isEqualTo(mockedPostResponse.id());
        assertThat(response.title()).isEqualTo(mockedPostResponse.title());
        assertThat(response.createdAt()).isNotNull();

        verify(postMapper, times(1)).toEntity(validRequest);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postMapper, times(1)).toResponseDTO(mockedPostEntity);
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

        PostResponseDTO result = postService.findById(mockedPostEntity.getId());

        assertThat(result.id()).isEqualTo(mockedPostResponse.id());
        assertThat(result.title()).isEqualTo(mockedPostResponse.title());

        verify(postRepository, times(1)).findById(mockedPostEntity.getId());
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when id does not exist")
    void shouldThrowPostNotFoundExceptionWhenIdDoesNotExist() {
        String invalidId = "68e0234a70424186e056e45f";

        when(postRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findById(invalidId));

        verify(postRepository, times(1)).findById(invalidId);
        verify(postMapper, never()).toResponseDTO(any());
    }


    @Test
    @DisplayName("Should update only provided fields when given existing id and valid update data")
    void shouldUpdatePostWhenGivenExistingIdAndValidRequestData() {
        String newDescription = "Updated description";
        PostUpdateDTO updateDTO = new PostUpdateDTO(null, newDescription, null);

        Post updatedPost = Post.builder()
                               .id(mockedPostEntity.getId())
                               .title(mockedPostEntity.getTitle())
                               .description(newDescription)
                               .body(mockedPostEntity.getBody())
                               .createdAt(mockedPostEntity.getCreatedAt())
                               .updatedAt(Instant.now())
                               .build();

        PostResponseDTO updatedResponse = new PostResponseDTO(
                updatedPost.getId(),
                updatedPost.getTitle(),
                updatedPost.getDescription(),
                updatedPost.getBody(),
                updatedPost.getCreatedAt(),
                updatedPost.getUpdatedAt()
        );

        when(postRepository.findById(mockedPostEntity.getId())).thenReturn(Optional.of(mockedPostEntity));
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(postMapper.toResponseDTO(any(Post.class))).thenReturn(updatedResponse);


        PostResponseDTO response = postService.update(mockedPostEntity.getId(), updateDTO);

        assertThat(response.description()).isEqualTo(newDescription);
        assertThat(response.title()).isEqualTo(mockedPostEntity.getTitle());
        assertThat(response.body()).isEqualTo(mockedPostEntity.getBody());

        verify(postRepository, times(1)).findById(mockedPostEntity.getId());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postMapper, times(1)).toResponseDTO(any(Post.class));
    }

    @Test
    @DisplayName("Should update all fields when given existing id and complete update data")
    void shouldUpdateAllFieldsWhenGivenCompleteData() {
        String newTitle = "New Title";
        String newDescription = "New Description";
        String newBody = "New Body";
        PostUpdateDTO updateDTO = new PostUpdateDTO(newTitle, newDescription, newBody);

        Post updatedPost = Post.builder()
                               .id(mockedPostEntity.getId())
                               .title(newTitle)
                               .description(newDescription)
                               .body(newBody)
                               .createdAt(mockedPostEntity.getCreatedAt())
                               .updatedAt(Instant.now())
                               .build();

        PostResponseDTO updatedResponse = new PostResponseDTO(
                updatedPost.getId(),
                newTitle,
                newDescription,
                newBody,
                updatedPost.getCreatedAt(),
                updatedPost.getUpdatedAt()
        );

        when(postRepository.findById(mockedPostEntity.getId())).thenReturn(Optional.of(mockedPostEntity));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(postMapper.toResponseDTO(any(Post.class))).thenReturn(updatedResponse);

        PostResponseDTO response = postService.update(mockedPostEntity.getId(), updateDTO);

        assertThat(response.title()).isEqualTo(newTitle);
        assertThat(response.description()).isEqualTo(newDescription);
        assertThat(response.body()).isEqualTo(newBody);

        verify(postRepository, times(1)).findById(mockedPostEntity.getId());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("Should throw PostNotFoundException when updating non-existent post")
    void shouldThrowPostNotFoundExceptionWhenUpdatingNonExistentPost() {
        String invalidId = "68e0234a70424186e056e45f";
        PostUpdateDTO requestDTO = new PostUpdateDTO(VALID_TITLE, VALID_DESCRIPTION, VALID_BODY);

        when(postRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.update(invalidId, requestDTO));

        verify(postRepository, times(1)).findById(invalidId);
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidArgumentException when all update fields are null")
    void shouldThrowInvalidArgumentExceptionWhenAllFieldsAreNull() {
        PostUpdateDTO invalidRequest = new PostUpdateDTO(null, null, null);

        when(postRepository.findById(mockedPostEntity.getId())).thenReturn(Optional.of(mockedPostEntity));

        assertThrows(InvalidArgumentException.class,
                () -> postService.update(mockedPostEntity.getId(), invalidRequest));

        verify(postRepository, times(1)).findById(mockedPostEntity.getId());
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidArgumentException when all update fields are blank")
    void shouldThrowInvalidArgumentExceptionWhenAllFieldsAreBlank() {
        PostUpdateDTO invalidRequest = new PostUpdateDTO("", "", "");

        when(postRepository.findById(mockedPostEntity.getId())).thenReturn(Optional.of(mockedPostEntity));

        assertThrows(InvalidArgumentException.class,
                () -> postService.update(mockedPostEntity.getId(), invalidRequest));

        verify(postRepository, times(1)).findById(mockedPostEntity.getId());
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidArgumentException when all update fields are whitespace")
    void shouldThrowInvalidArgumentExceptionWhenAllFieldsAreWhitespace() {
        PostUpdateDTO invalidRequest = new PostUpdateDTO("   ", "   ", "   ");

        when(postRepository.findById(mockedPostEntity.getId())).thenReturn(Optional.of(mockedPostEntity));

        assertThrows(InvalidArgumentException.class,
                () -> postService.update(mockedPostEntity.getId(), invalidRequest));

        verify(postRepository, times(1)).findById(mockedPostEntity.getId());
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidArgumentException when no valid field provided with mixed null/blank")
    void shouldThrowInvalidArgumentExceptionWhenMixedNullAndBlank() {
        PostUpdateDTO invalidRequest = new PostUpdateDTO(null, "", "   ");

        when(postRepository.findById(mockedPostEntity.getId())).thenReturn(Optional.of(mockedPostEntity));

        assertThrows(InvalidArgumentException.class,
                () -> postService.update(mockedPostEntity.getId(), invalidRequest));

        verify(postRepository, times(1)).findById(mockedPostEntity.getId());
        verify(postRepository, never()).save(any());
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
    @DisplayName("Should throw PostNotFoundException when deleting non-existent post")
    void shouldThrowPostNotFoundExceptionWhenDeletingNonExistentPost() {
        String invalidId = "68e0234a70424186e056e45f";

        when(postRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.delete(invalidId));

        verify(postRepository, times(1)).findById(invalidId);
        verify(postRepository, never()).deleteById(invalidId);
    }
}
