package net.javaguides.springboot.controller;

import net.javaguides.springboot.dto.CommentDto;
import net.javaguides.springboot.dto.PostDto;
import net.javaguides.springboot.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BlogController {
    private PostService postService;

    public BlogController(PostService postService) {
        this.postService = postService;
    }

    // handler method to handle http://localhost:8080/
   /* @GetMapping("/")
    public String viewBlogPosts(Model model){
        List<PostDto> postsResponse = postService.findAllPosts();
        model.addAttribute("postsResponse", postsResponse);
        return "blog/view_posts";
    } */
    @GetMapping("/")
    public String viewBlogPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "9") int size,
                        Model model) {
        // Fetch paginated posts
        Page<PostDto> postsResponse = postService.findPaginatedPosts(page, size);

        model.addAttribute("postsResponse", postsResponse);
        return "blog/view_posts";
    }

    // handler method to handle view post request
    @GetMapping("/post/{postUrl}")
    private String showPost(@PathVariable("postUrl") String postUrl,
                            Model model){
        PostDto post = postService.findPostByUrl(postUrl);
        CommentDto comment = new CommentDto();
        model.addAttribute("post", post);
        model.addAttribute("comment", comment);
        return "blog/blog_post";
    }

    // handler method to handle blog post search request
    // http://localhost:8080/page/search?query=java
    @GetMapping("/page/search")
    public String searchPosts(@RequestParam(value = "query") String query,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "9") int size,
                              Model model){
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDto> postsResponse = postService.searchPosts(query,pageable);
        model.addAttribute("postsResponse", postsResponse);
        return "blog/view_posts";
    }

}
