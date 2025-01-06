package net.javaguides.springboot.repository;

import net.javaguides.springboot.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByUrl(String url);

    @Query("SELECT p FROM Post p WHERE " +
            "p.title LIKE CONCAT('%', :query, '%') OR " +
            "p.shortDescription LIKE CONCAT('%', :query, '%') " )
    Page<Post> searchPosts(String query, Pageable pageable);

    @Query(value = "SELECT * FROM posts WHERE created_by = :userId", nativeQuery = true)
    Page<Post> findPostsByUser(@Param("userId") Long userId, Pageable pageable);



}
