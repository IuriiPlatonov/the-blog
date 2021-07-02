package org.example.theblog.api.response;

import lombok.Data;
import org.example.theblog.api.response.DTO.UserPost;

import java.util.List;

@Data
public class PostResponse {

    private int count;
    private List<UserPost> posts;
}
