package org.example.theblog.api.response.DTO;

import lombok.Data;

@Data
public class UserPost {

    private int id;
    private long timestamp;
    private UserName user;
    private String title;
    private String announce;
    private long likeCount;
    private long dislikeCount;
    private int commentCount;
    private int viewCount;

    public UserPost(int id, long timestamp, UserName user, String title, String announce, long likeCount,
                    long dislikeCount, int commentCount, int viewCount) {
        this.id = id;
        this.timestamp = timestamp;
        this.user = user;
        this.title = title;
        this.announce = announce;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
    }
}
