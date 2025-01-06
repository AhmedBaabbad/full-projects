package net.javaguides.springboot.service.impl;

import net.javaguides.springboot.dto.PostDto;
import net.javaguides.springboot.entity.Post;
import net.javaguides.springboot.entity.User;
import net.javaguides.springboot.mapper.PostMapper;
import net.javaguides.springboot.repository.PostRepository;
import net.javaguides.springboot.repository.UserRepository;
import net.javaguides.springboot.service.PostService;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.util.ApiUtils;
import net.javaguides.springboot.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.javaguides.springboot.mapper.PostMapper.mapToPostDto;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

  /*  @Override
    public List<PostDto> findAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(PostMapper::mapToPostDto)
                .collect(Collectors.toList());
    } */

    @Override
    public Page<PostDto> findAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return  posts
                .map(post -> mapToPostDto(post));

    }

    @Override
    public void savePost(PostDto postDto) {
        String email = SecurityUtils.getCurrentUser().getUsername();
        User user = userRepository.findByEmail(email);
        String generatedUrl = ApiUtils.getUrl(postDto.getTitle());
        // Check if the URL already exists in the database
        Optional<Post> existingPost = postRepository.findByUrl(generatedUrl);
        if (existingPost.isPresent()) {
            throw new IllegalArgumentException("Cannot save using this title because it already exists in the database: " + generatedUrl);
        }
        Post post = PostMapper.mapToPost(postDto);
        post.setCreatedBy(user);
        post.setUrl(generatedUrl);
        postRepository.save(post);
    }

    @Override
    public Page<PostDto> findPostsByUser(Pageable pageable) {
        String email = SecurityUtils.getCurrentUser().getUsername();
        User createdBy = userRepository.findByEmail(email);
        Long userId = createdBy.getId();
       // Page<Post> posts = postRepository.findPostsByUser(userId,pageable);
        return  postRepository.findPostsByUser(userId,pageable)
                .map(post -> mapToPostDto(post));


    }

    @Override
    public PostDto findPostById(Long postId) {
        Post post = postRepository.findById(postId).get();
        return mapToPostDto(post);
    }

    @Override
    public void updatePost(PostDto postDto) {
        String email = SecurityUtils.getCurrentUser().getUsername();
        User createdBy = userRepository.findByEmail(email);
        Post post = PostMapper.mapToPost(postDto);
        post.setCreatedBy(createdBy);
        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public PostDto findPostByUrl(String postUrl) {
        Post post = postRepository.findByUrl(postUrl).get();
        return mapToPostDto(post);
    }

    @Override
    public Page<PostDto> searchPosts(String query, Pageable pageable) {
      /*  Page<Post> posts = postRepository.searchPosts(query, pageable);
        return posts.stream()
                .map(PostMapper::mapToPostDto)
                .collect(Collectors.toList()); */
        return  postRepository.searchPosts(query, pageable)
                .map(post -> mapToPostDto(post));
    }

    @Override
    public Page<PostDto> findPaginatedPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return postRepository.findAll(pageRequest)
                .map(post -> mapToPostDto(post));
    }
}
