package com.laporeon.posts_api.services;

import com.laporeon.posts_api.dto.request.PostRequestDTO;
import com.laporeon.posts_api.dto.request.PostUpdateDTO;
import com.laporeon.posts_api.dto.response.PageResponseDTO;
import com.laporeon.posts_api.dto.response.PostResponseDTO;
import com.laporeon.posts_api.entities.Post;
import com.laporeon.posts_api.exceptions.custom.PostNotFoundException;
import com.laporeon.posts_api.mappers.PostMapper;
import com.laporeon.posts_api.repositories.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

        post.update(dto.title(), dto.description(), dto.body());

        postRepository.save(post);

        return postMapper.toResponseDTO(post);
    }

    public void delete(String id) {
        postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));

        postRepository.deleteById(id);
    }



}
