package org.example.theblog.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDateCountResponse {
    private String data;
    private long count;
}