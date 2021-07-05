package org.example.theblog.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    record AuthorizedUser(int id, String name, String photo, String email, boolean moderation, int moderationCount,
                          boolean settings) {
    }

    public record AuthResponse(boolean result, AuthorizedUser user) {
    }

    public AuthResponse getAuth() {
        return new AuthResponse(false, null);
    }
}
