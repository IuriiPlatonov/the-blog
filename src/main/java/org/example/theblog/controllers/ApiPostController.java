package org.example.theblog.controllers;

import org.example.theblog.api.response.PostResponse;
import org.example.theblog.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post")
    private ResponseEntity<PostResponse> getPosts(@RequestParam int offset, int limit, String mode) {
        return ResponseEntity.ok(postService.getPosts(offset, limit, mode));
    }

    @GetMapping("/post/search")
    private ResponseEntity<PostResponse> searchPosts(@RequestParam int offset, int limit, String query) {
        return ResponseEntity.ok(postService.searchPosts(offset, limit, query));
    }
}
