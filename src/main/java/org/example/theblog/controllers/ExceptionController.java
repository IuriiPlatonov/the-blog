package org.example.theblog.controllers;

import org.example.theblog.exceptions.PostCommentException;
import org.example.theblog.exceptions.UserUnauthorizedException;
import org.example.theblog.service.CommentService;
import org.example.theblog.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ImageService.ImageResponse> postImageException() {
        return new ResponseEntity<>(new ImageService.ImageResponse(false,
                Map.of("image", "Размер файла превышает допустимый размер")), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostCommentException.class)
    public ResponseEntity<CommentService.CommentResponse> postCommentException(PostCommentException e) {
        return new ResponseEntity<>(e.getCommentResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    public ResponseEntity<?> getAllStatisticsException() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
