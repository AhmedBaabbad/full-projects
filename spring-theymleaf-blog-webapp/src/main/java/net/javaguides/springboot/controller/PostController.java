package net.javaguides.springboot.controller;

import jakarta.validation.Valid;
import net.javaguides.springboot.dto.CommentDto;
import net.javaguides.springboot.dto.PostDto;
import net.javaguides.springboot.service.CommentService;
import net.javaguides.springboot.service.PostService;
import net.javaguides.springboot.util.ApiUtils;
import net.javaguides.springboot.util.Constants;
import net.javaguides.springboot.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    private PostService postService;
    private CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;

    }

    // create handler method, GET request and return model and view
    @GetMapping("/admin/posts")
    public String posts(Model model) {
        String role = SecurityUtils.getRole();
        List<PostDto> posts = null;
        if(Constants.ROLE_ADMIN.equals(role)){
            posts = postService.findAllPosts();
        }else{
            posts = postService.findPostsByUser();
        }
        model.addAttribute("posts", posts);
        return "/admin/posts";
    }

  /*  @GetMapping("/admin/posts")
    public String posts(@RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "9") int size,
                        Model model) {
        // Fetch paginated posts
        Page<PostDto> posts = postService.findPaginatedPosts(page, size);

        model.addAttribute("posts", posts);
        return "/admin/posts";
    } */

    @GetMapping("admin/posts/newpost")
    public String newPostForm(Model model) {
        PostDto postDto = new PostDto();
        model.addAttribute("post", postDto);
        return "admin/create_post";
    }

    @PostMapping("/admin/posts")
    public String createPost(@Valid @ModelAttribute("post") PostDto postDto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("post", postDto);
            return "admin/create_post";
        }
        model.addAttribute("post", postDto);
        postDto.setUrl(ApiUtils.getUrl(postDto.getTitle()));
        postService.savePost(postDto);
        return "redirect:/admin/post-success?action=create";
    }

    /*@GetMapping("/admin/post-success")
   public String showPostSuccessPage() {
       return "admin/post-success"; // This should match the template file name.
   } */
    @GetMapping("/admin/post-success")
    public String showPostSuccessPage(@RequestParam("action") String action, Model model) {
        model.addAttribute("action", action); // Pass the action type to the view
        return "admin/post-success"; // Return the post-success.html page
    }
    // handler method to handle edit post request

    @GetMapping("/admin/posts/{postId}/edit")
    public String editPostForm(@PathVariable("postId") Long postId,
                               Model model) {

        PostDto postDto = postService.findPostById(postId);
        model.addAttribute("post", postDto);
        return "admin/edit_post";

    }

    @PostMapping("/admin/posts/{postId}")
    public String updatePost(@PathVariable("postId") Long postId,
                             @Valid @ModelAttribute("post") PostDto post,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("post", post);
            return "admin/edit_post";
        }

        post.setId(postId);
        postService.updatePost(post);
        return "redirect:/admin/post-success?action=update";
    }

    // handler method to handle delete post request
    @GetMapping("/admin/posts/{postId}/delete")
    public String deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);
        return "redirect:/admin/post-success?action=delete";

    }
    @GetMapping("/admin/posts/{postUrl}/view")
    public String viewPost(@PathVariable("postUrl") String postUrl,
                           Model model){
        PostDto postDto = postService.findPostByUrl(postUrl);
        model.addAttribute("post", postDto);
        return "admin/view_post";

    }
    // A method to handle the search process to gat all related posts with assigned query
    @GetMapping("/admin/posts/search")
    public String searchPosts(@RequestParam(value = "query") String query,
                              Model model) {
        List<PostDto> posts = postService.searchPosts(query);
        model.addAttribute("posts", posts);
        return "admin/posts";
    }
    // handler method to handle list comments request
    @GetMapping("/admin/posts/comments")
    public String postComments(Model model){
        String role = SecurityUtils.getRole();
        List<CommentDto> comments = null;
        if(Constants.ROLE_ADMIN.equals(role)){
            comments = commentService.findAllComments();
        }else{
            comments = commentService.findCommentsByPost();
        }
        model.addAttribute("comments", comments);
        return "admin/comments";
    }
    // handler method to handle delete comment request
    @GetMapping("/admin/posts/comments/{commentId}")
    public String deleteComment(@PathVariable("commentId") Long commentId){
        commentService.deleteComment(commentId);
        return "redirect:/admin/posts/comments";
    }

}
