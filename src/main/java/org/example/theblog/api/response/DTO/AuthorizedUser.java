package org.example.theblog.api.response.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorizedUser extends UserName {

    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}
