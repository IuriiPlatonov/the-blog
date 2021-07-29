package org.example.theblog.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import org.example.theblog.model.entity.PostComment;
import org.example.theblog.model.repository.PostCommentRepository;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;

    public CommentResponse postComment(CommentRequest request, Principal principal) {
        Map<String, String> errors = new HashMap<>();
        boolean result = true;
        Integer id = null;

        if (request.postId() == null
            || request.postId().isBlank()
            || postRepository.findById(Integer.parseInt(request.postId())).isEmpty()) {
            result = false;
        }

        if (request.parentId() != null && !request.parentId().isBlank()
            && postCommentRepository.findById(Integer.parseInt(request.parentId())).isEmpty()) {
            result = false;
            System.out.println("БЛОТ");
        }

        if (request.text().isBlank()) {
            result = false;
            errors.put("text", "Текст комментария не задан или слишком короткий");
        }

        if (result) {
            PostComment postComment = new PostComment();
            postComment.setPostId(Integer.parseInt(request.postId()));
            if (request.parentId() != null) {
                postComment.setParent(postCommentRepository.getById(Integer.parseInt(request.parentId())));
            }
            postComment.setUser(userRepository.findUsersByEmail(principal.getName()));
            postComment.setTime(LocalDateTime.now());
            postComment.setText(request.text());
            id = postCommentRepository.save(postComment).getId();

        }
        return new CommentResponse(id, result, errors);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CommentResponse(Integer id, Boolean result, Map<String, String> errors) {
    }

    public record CommentRequest(@JsonProperty("parent_id") String parentId, @JsonProperty("post_id") String postId,
                                 String text) {
    }
}
