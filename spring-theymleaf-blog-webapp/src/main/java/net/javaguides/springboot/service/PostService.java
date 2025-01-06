package net.javaguides.springboot.service;

import net.javaguides.springboot.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    // List<PostDto> findAllPosts();
    Page<PostDto> findAllPosts(Pageable pageable);
    void savePost(PostDto postDto);
    Page<PostDto> findPostsByUser(Pageable pageable);

    PostDto findPostById(Long postId);

    void updatePost(PostDto postDto);

    void deletePost(Long postId);

    PostDto findPostByUrl(String postUrl);

    Page<PostDto> searchPosts(String query, Pageable pageable);

    Page<PostDto> findPaginatedPosts(int page, int size);
}
