package org.example.theblog.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import org.example.theblog.model.entity.ModerationStatus;
import org.example.theblog.model.entity.Post;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@AllArgsConstructor
public class ModerationService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ResponseEntity<ModerationResponse> postModeration(ModerationRequest request, Principal principal) {
        Post post = postRepository.getById(request.id());
        User moderator = userRepository.findUsersByEmail(principal.getName());

        switch (request.decision()){
            case "accept" -> {
                post.setModerationStatus(ModerationStatus.ACCEPTED);
                post.setModerator(moderator);
                postRepository.flush();
            }

            case "decline" -> {
                post.setModerationStatus(ModerationStatus.DECLINED);
                post.setModerator(moderator);
                postRepository.flush();
            }
        }

        return ResponseEntity.ok(new ModerationResponse(true));
    }

    public record ModerationResponse(boolean result) {
    }

    public record ModerationRequest(@JsonProperty("post_id") int id, String decision) {
    }
}
