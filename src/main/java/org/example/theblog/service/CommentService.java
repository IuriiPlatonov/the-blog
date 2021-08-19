package org.example.theblog.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import org.example.theblog.model.entity.PostComment;
import org.example.theblog.model.repository.PostCommentRepository;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<?> postComment(CommentRequest request, Principal principal) {
        Map<String, String> errors = new HashMap<>();
        boolean correctParentId = request.parentId() != null &&
                                  request.parentId().matches("\\d+");

        boolean wrongPostId = request.postId() == null ||
                              !request.postId().matches("\\d+");

        boolean emptyText = request.text().isBlank() ||
                            request.text().matches("((&nbsp;)( )?)*");

        int postId = wrongPostId ? 0 : Integer.parseInt(request.postId());
        int parentId = correctParentId ? Integer.parseInt(request.parentId()) : 0;
        boolean result = true;
        Integer id = null;

        if (wrongPostId ||
            postRepository.findById(postId).isEmpty()) {
            result = false;
        }

        if (correctParentId &&
            postCommentRepository.findById(parentId).isEmpty()) {
            result = false;
        }

        if (emptyText) {
            result = false;
            errors.put("text", "Текст комментария не задан или слишком короткий");
        }

        if (result) {
            PostComment postComment = new PostComment();
            postComment.setPostId(postId);
            if (correctParentId) {
                postComment.setParent(postCommentRepository.getById(parentId));
            }
            postComment.setUser(userRepository.findUsersByEmail(principal.getName()));
            postComment.setTime(LocalDateTime.now());
            postComment.setText(request.text());
            id = postCommentRepository.save(postComment).getId();

        }
        return result
                ? ResponseEntity.ok(id)
                : new ResponseEntity<>(new CommentResponse(false, errors), HttpStatus.BAD_REQUEST);

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CommentResponse(Boolean result, Map<String, String> errors) {
    }

    public record CommentRequest(@JsonProperty("parent_id") String parentId, @JsonProperty("post_id") String postId,
                                 String text) {
    }
}
