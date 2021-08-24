package org.example.theblog.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import org.example.theblog.model.entity.ModerationStatus;
import org.example.theblog.model.entity.Post;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ModerationService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ModerationResponse postModeration(ModerationRequest request, Principal principal) {
        Optional<Post> post = postRepository.findById(request.id());
        Optional<User> moderator = userRepository.findByEmail(principal.getName());

        if (post.isPresent() &&
            moderator.isPresent() &&
            moderator.get().getIsModerator() == 1) {
            switch (request.decision()) {
                case "accept" -> {
                    post.get().setModerationStatus(ModerationStatus.ACCEPTED);
                    post.get().setModerator(moderator.get());
                    postRepository.flush();
                }

                case "decline" -> {
                    post.get().setModerationStatus(ModerationStatus.DECLINED);
                    post.get().setModerator(moderator.get());
                    postRepository.flush();
                }
            }
            return new ModerationResponse(true);
        }
        return new ModerationResponse(false);
    }

    public record ModerationResponse(boolean result) {
    }

    public record ModerationRequest(@JsonProperty("post_id") int id, String decision) {
    }
}
