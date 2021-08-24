package org.example.theblog.service;

import lombok.RequiredArgsConstructor;
import org.example.theblog.exceptions.UserUnauthorizedException;
import org.example.theblog.model.entity.GlobalSetting;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.GlobalSettingRepository;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final PostRepository postRepository;
    private final GlobalSettingRepository globalSettingRepository;
    private final UserRepository userRepository;

    public StatisticsResponse getMyStatistics(Principal principal) {
        String email = principal.getName();

        return new StatisticsResponse(postRepository.getMyPostCount(email).orElse(0L),
                postRepository.getMyLikeCount(email).orElse(0),
                postRepository.getMyDislikeCount(email).orElse(0),
                postRepository.getMyViewCount(email).orElse(0),
                postRepository.getDateMyFirstPost(email).orElse(LocalDateTime.now()).toEpochSecond(ZoneOffset.UTC));
    }

    public StatisticsResponse getAllStatistics(Principal principal) {

        if (Objects.nonNull(principal)) {
            GlobalSetting globalSetting = globalSettingRepository.findGlobalSettingByCode("STATISTICS_IS_PUBLIC");
            Optional<User> user = userRepository.findByEmail(principal.getName());
            boolean publicStatisticsDisable = globalSetting.getValue().equals("NO");
            boolean userIsNotModerator = (user.isPresent()) && (user.get().getIsModerator() == 0);

            if (publicStatisticsDisable && userIsNotModerator) {
                throw new UserUnauthorizedException();
            }
        }

        return new StatisticsResponse(postRepository.count(),
                postRepository.getLikeCount().orElse(0),
                postRepository.getDislikeCount().orElse(0),
                postRepository.getViewCount().orElse(0),
                postRepository.getDateFirstPost().orElse(LocalDateTime.now()).toEpochSecond(ZoneOffset.UTC));
    }


    public record StatisticsResponse(Long postsCount, Integer likesCount, Integer dislikesCount,
                                     Integer viewsCount, Long firstPublication) {
    }
}
