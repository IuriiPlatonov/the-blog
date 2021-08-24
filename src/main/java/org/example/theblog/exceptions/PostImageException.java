package org.example.theblog.exceptions;

import org.example.theblog.service.ImageService;

public class PostImageException extends RuntimeException{
    private ImageService.ImageResponse imageResponse;

    public ImageService.ImageResponse getCommentResponse() {
        return imageResponse;
    }

    public PostImageException(ImageService.ImageResponse imageResponse) {
        this.imageResponse = imageResponse;
    }
}
