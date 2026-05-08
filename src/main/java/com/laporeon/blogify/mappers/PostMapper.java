package com.laporeon.splog.mappers;

import com.laporeon.splog.dto.request.PostRequestDTO;
import com.laporeon.splog.dto.response.PageResponseDTO;
import com.laporeon.splog.dto.response.PostResponseDTO;
import com.laporeon.splog.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostResponseDTO toResponseDTO(Post post) {
        return new PostResponseDTO(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getBody(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    public Post toEntity(PostRequestDTO dto) {
        return Post
                .builder()
                .title(dto.title())
                .description(dto.description())
                .body(dto.body())
                .build();
    }

    public PageResponseDTO<PostResponseDTO> toPageResponseDTO(Page<Post> posts) {
        return new PageResponseDTO<>(
                posts.getContent().stream().map(this::toResponseDTO).toList(),
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalPages(),
                posts.getTotalElements(),
                posts.getNumberOfElements(),
                posts.isFirst(),
                posts.isLast(),
                posts.isEmpty(),
                posts.getSort().isSorted(),
                posts.getSort().isUnsorted()
        );
    }

}
