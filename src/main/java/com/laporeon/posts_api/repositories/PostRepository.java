package com.laporeon.posts_api.repositories;

import com.laporeon.posts_api.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;

public interface PostRepository extends MongoRepository<Post, String> {

    Page<Post> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Post> findByCreatedAtGreaterThanEqual(LocalDateTime startDate, Pageable pageable);

    Page<Post> findByCreatedAtLessThanEqual(LocalDateTime endDate, Pageable pageable);

}
