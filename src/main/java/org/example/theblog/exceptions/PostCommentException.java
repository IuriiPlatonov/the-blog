package org.example.theblog.exceptions;

import org.example.theblog.service.CommentService;

public class PostCommentException extends RuntimeException{
    private final CommentService.CommentResponse commentResponse;

    public CommentService.CommentResponse getCommentResponse() {
        return commentResponse;
    }

    public PostCommentException(CommentService.CommentResponse commentResponse) {
        this.commentResponse = commentResponse;
    }
}
