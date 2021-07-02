package org.example.theblog.api.response;

import lombok.Data;
import org.example.theblog.api.response.DTO.AuthorizedUser;

@Data
public class AuthResponse {

    private boolean result;
    private AuthorizedUser user;
}
