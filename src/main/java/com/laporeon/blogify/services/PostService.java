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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public PostResponseDTO update(String id, @Valid PostUpdateDTO dto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        validateChangesAndApply(dto, post);
        postRepository.save(post);
        return postMapper.toResponseDTO(post);
    }

    @Transactional
    public void delete(String id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        postRepository.delete(post);
    }

    private void validateChangesAndApply(PostUpdateDTO dto, Post post) {
        boolean hasTitleChanged = dto.title() != null && !dto.title().isBlank() && !dto.title().equals(post.getTitle());
        boolean hasDescriptionChanged = dto.description() != null  && !dto.description().isBlank() && !dto.description().equals(post.getDescription());
        boolean hasBodyChanged = dto.body() != null  && !dto.body().isBlank()  && !dto.body().equals(post.getBody());

        if (!hasTitleChanged && !hasDescriptionChanged && !hasBodyChanged) {
            throw new InvalidArgumentException("No changes detected.");
        }

        if(hasTitleChanged) post.setTitle(dto.title());
        if(hasDescriptionChanged) post.setDescription(dto.description());
        if(hasBodyChanged) post.setBody(dto.body());
    }

}
