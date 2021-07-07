package org.example.theblog.controllers;

import lombok.AllArgsConstructor;
import org.example.theblog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    @GetMapping("/post")
    private ResponseEntity<PostService.SmallViewPostResponse> getPosts(@RequestParam int offset, int limit, String mode) {
        return ResponseEntity.ok(postService.getPosts(offset, limit, mode));
    }

    @GetMapping("/post/search")
    private ResponseEntity<PostService.SmallViewPostResponse> searchPosts(@RequestParam int offset, int limit, String query) {
        return ResponseEntity.ok(postService.searchPosts(offset, limit, query));
    }

    @GetMapping("/post/byDate")
    private ResponseEntity<PostService.SmallViewPostResponse> searchPostsByDate(@RequestParam int offset, int limit, String date) {
        return ResponseEntity.ok(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/post/byTag")
    private ResponseEntity<PostService.SmallViewPostResponse> searchPostsByTag(@RequestParam int offset, int limit, String tag) {
        return ResponseEntity.ok(postService.getPostsByTag(offset, limit, tag));
    }

    @GetMapping("/post/{id}")
    private ResponseEntity<PostService.FullViewPostResponse> getPostsByID(@PathVariable String id) {
        PostService.FullViewPostResponse response = postService.getPostsByID(Integer.parseInt(id));
        return response == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
                : ResponseEntity.ok(response);
    }
}
