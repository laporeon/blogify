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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostMapper postMapper;

    public PostResponseDTO create(PostRequestDTO dto) {
        Post post = postRepository.save(postMapper.toEntity(dto));
        return postMapper.toResponseDTO(post);
    }

    public PageResponseDTO<PostResponseDTO> listPosts(Pageable pageable, LocalDate startDate, LocalDate endDate) {
        Page<Post> posts;

        if (startDate != null && endDate != null) {
            posts = postRepository.findByCreatedAtBetween(startDate.atStartOfDay(), endDate.atTime(23,59,59), pageable);
        } else if (startDate != null) {
            posts = postRepository.findByCreatedAtGreaterThanEqual(startDate.atStartOfDay(), pageable);
        } else if (endDate != null) {
            posts = postRepository.findByCreatedAtLessThanEqual(endDate.atTime(23,59,59), pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }

        return postMapper.toPageResponseDTO(posts);
    }

    public PostResponseDTO findById(String id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        return postMapper.toResponseDTO(post);
    }

    public PostResponseDTO update(String id, @Valid PostUpdateDTO dto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));

        validateUpdateOptions(dto);

        if (dto.title() != null) post.setTitle(dto.title());
        if (dto.description() != null) post.setDescription(dto.description());
        if (dto.body() != null) post.setBody(dto.body());
        post.setUpdatedAt(Instant.now());

        postRepository.save(post);

        return postMapper.toResponseDTO(post);
    }

    public void delete(String id) {
        postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        postRepository.deleteById(id);
    }

    private static void validateUpdateOptions(PostUpdateDTO dto) {
        boolean hasTitle = dto.title() != null && !dto.title().isBlank();
        boolean hasDescription = dto.description() != null  && !dto.description().isBlank();
        boolean hasBody = dto.body() != null  && !dto.body().isBlank();

        if (!hasTitle && !hasDescription && !hasBody) {
            throw new InvalidArgumentException("Provide at least one field to update: title, description, or body.");
        }
    }

}
