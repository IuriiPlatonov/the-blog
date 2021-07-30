package org.example.theblog.controllers;

import lombok.RequiredArgsConstructor;
import org.example.theblog.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    @GetMapping("/post")
    public ResponseEntity<PostService.SmallViewPostResponse> getPosts(
            @RequestParam int offset, int limit, String mode) {
        return ResponseEntity.ok(postService.getPosts(offset, limit, mode));
    }

    @GetMapping("/post/search")
    //  @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostService.SmallViewPostResponse> searchPosts(@RequestParam int offset, int limit, String query) {
        return ResponseEntity.ok(postService.searchPosts(offset, limit, query));
    }

    @GetMapping("/post/byDate")
    public ResponseEntity<PostService.SmallViewPostResponse> searchPostsByDate(@RequestParam int offset, int limit, String date) {
        return ResponseEntity.ok(postService.getPostsByDate(offset, limit, date));
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<PostService.SmallViewPostResponse> searchPostsByTag(@RequestParam int offset, int limit, String tag) {
        return ResponseEntity.ok(postService.getPostsByTag(offset, limit, tag));
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostService.FullViewPostResponse> getPostsByID(@PathVariable int id, Principal principal) {
        return ResponseEntity.ok(postService.getPostsByID(id, principal));
    }

    @GetMapping("/post/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostService.SmallViewPostResponse> getMyPosts(
            @RequestParam int offset, int limit, String status, Principal principal) {
        return ResponseEntity.ok(postService.getMyPosts(offset, limit, status, principal));
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostService.WritePostResponse> writePost(@RequestBody PostService.PostRequest request, Principal principal) {
        return ResponseEntity.ok(postService.writePost(request, principal));
    }

    @PutMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostService.WritePostResponse> editPost(@RequestBody PostService.PostRequest request, @PathVariable int id) {
        return ResponseEntity.ok(postService.editPost(request, id));
    }

    @PostMapping("/post/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostService.VotesResponse> likePost(@RequestBody PostService.VotesRequest request, Principal principal) {
        return ResponseEntity.ok(postService.likePost(request, principal));
    }

    @PostMapping("/post/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostService.VotesResponse> dislikePost(@RequestBody PostService.VotesRequest request, Principal principal) {
        return ResponseEntity.ok(postService.dislikePost(request, principal));
    }
}
