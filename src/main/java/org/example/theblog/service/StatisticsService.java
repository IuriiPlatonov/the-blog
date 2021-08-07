package org.example.theblog.service;

import lombok.RequiredArgsConstructor;
import org.example.theblog.model.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final PostRepository postRepository;
    public StatisticsResponse getMyStatistics(Principal principal) {
        String email = principal.getName();

        return new StatisticsResponse(postRepository.getMyPostCount(email),
                postRepository.getMyLikeCount(email),
                postRepository.getMyDislikeCount(email),
                postRepository.getMyViewCount(email),
                postRepository.getDateMyFirstPost(email).toEpochSecond(ZoneOffset.UTC));
    }

    public record StatisticsResponse(int postsCount, int likesCount, int dislikesCount, int viewsCount, long firstPublication) {
    }
}
