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
        return postService.getPosts(offset, limit, mode);
    }

    @GetMapping("/post/search")
    public ResponseEntity<PostService.SmallViewPostResponse> searchPosts(@RequestParam int offset, int limit, String query) {
        return postService.searchPosts(offset, limit, query);
    }

    @GetMapping("/post/byDate")
    public ResponseEntity<PostService.SmallViewPostResponse> searchPostsByDate(@RequestParam int offset, int limit, String date) {
        return postService.getPostsByDate(offset, limit, date);
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<PostService.SmallViewPostResponse> searchPostsByTag(@RequestParam int offset, int limit, String tag) {
        return postService.getPostsByTag(offset, limit, tag);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostService.FullViewPostResponse> getPostsByID(@PathVariable int id, Principal principal) {
        return postService.getPostsByID(id, principal);
    }

    @GetMapping("/post/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostService.SmallViewPostResponse> getMyPosts(
            @RequestParam int offset, int limit, String status, Principal principal) {
        return postService.getMyPosts(offset, limit, status, principal);
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostService.WritePostResponse> writePost(@RequestBody PostService.PostRequest request, Principal principal) {
        return postService.writePost(request, principal);
    }

    @PutMapping("/post/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostService.WritePostResponse> editPost(@RequestBody PostService.PostRequest request, @PathVariable int id) {
        return postService.editPost(request, id);
    }

    @PostMapping("/post/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostService.VotesResponse> likePost(@RequestBody PostService.VotesRequest request, Principal principal) {
        return postService.likePost(request, principal);
    }

    @PostMapping("/post/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostService.VotesResponse> dislikePost(@RequestBody PostService.VotesRequest request, Principal principal) {
        return postService.dislikePost(request, principal);
    }

    @GetMapping("/post/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostService.SmallViewPostResponse> getPostModeration(
            @RequestParam int offset, int limit, String status, Principal principal) {
        return postService.getPostModeration(offset, limit, status, principal);
    }
}
