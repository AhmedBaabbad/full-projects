package net.javaguides.springboot.service;

import net.javaguides.springboot.dto.PostDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    List<PostDto> findAllPosts();
    void savePost(PostDto postDto);
    List<PostDto> findPostsByUser();

    PostDto findPostById(Long postId);

    void updatePost(PostDto postDto);

    void deletePost(Long postId);

    PostDto findPostByUrl(String postUrl);

    List<PostDto> searchPosts(String query);

    Page<PostDto> findPaginatedPosts(int page, int size);
}
