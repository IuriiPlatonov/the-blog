package org.example.theblog.service;

import lombok.RequiredArgsConstructor;
import org.example.theblog.model.entity.GlobalSetting;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.GlobalSettingRepository;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final PostRepository postRepository;
    private final GlobalSettingRepository globalSettingRepository;
    private final UserRepository userRepository;

    public StatisticsResponse getMyStatistics(Principal principal) {
        String email = principal.getName();

        return new StatisticsResponse(postRepository.getMyPostCount(email),
                postRepository.getMyLikeCount(email),
                postRepository.getMyDislikeCount(email),
                postRepository.getMyViewCount(email),
                postRepository.getDateMyFirstPost(email).toEpochSecond(ZoneOffset.UTC));
    }

    public StatisticsResponse getAllStatistics(Principal principal) {
        GlobalSetting globalSetting = globalSettingRepository.findGlobalSettingByCode("STATISTICS_IS_PUBLIC");
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (globalSetting.getValue().equals("NO") && user != null && user.getIsModerator() == 0) {
                return null;
            }
        }

        return new StatisticsResponse(postRepository.count(),
                postRepository.getLikeCount(),
                postRepository.getDislikeCount(),
                postRepository.getViewCount(),
                postRepository.getDateFirstPost().toEpochSecond(ZoneOffset.UTC));
    }


    public record StatisticsResponse(long postsCount, int likesCount, int dislikesCount, int viewsCount, long firstPublication) {
    }
}
