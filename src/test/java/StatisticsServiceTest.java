import org.example.theblog.model.entity.GlobalSetting;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.GlobalSettingRepository;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.UserRepository;
import org.example.theblog.service.StatisticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

public class StatisticsServiceTest {

    PostRepository postRepository = Mockito.mock(PostRepository.class);
    GlobalSettingRepository globalSettingRepository = Mockito.mock(GlobalSettingRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    Principal principal = Mockito.mock(Principal.class);
    StatisticsService statisticsService =
            new StatisticsService(postRepository, globalSettingRepository, userRepository);


    @Test
    @DisplayName("Get all statistics, STATISTICS_IS_PUBLIC - YES")
    public void getAllStatisticsTest() {
        GlobalSetting globalSetting = new GlobalSetting();
        globalSetting.setCode("STATISTICS_IS_PUBLIC");
        globalSetting.setValue("YES");
        Mockito.when(globalSettingRepository.findGlobalSettingByCode("STATISTICS_IS_PUBLIC")).thenReturn(globalSetting);

        assertEquals(statisticsService.getAllStatistics(principal).getStatusCode(), HttpStatus.OK);
    }

    @Test
    @DisplayName("Get all statistics, STATISTICS_IS_PUBLIC - NO and user is NOT moderator")
    public void getAllStatisticsWithIsPublicDisableAndUserNoModeratorTest() {
        GlobalSetting globalSetting = new GlobalSetting();
        globalSetting.setCode("STATISTICS_IS_PUBLIC");
        globalSetting.setValue("NO");
        Mockito.when(globalSettingRepository.findGlobalSettingByCode("STATISTICS_IS_PUBLIC")).thenReturn(globalSetting);
        User user = new User();
        user.setIsModerator((byte) 0);
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertEquals(statisticsService.getAllStatistics(principal).getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Get all statistics, STATISTICS_IS_PUBLIC - NO and user is moderator")
    public void getAllStatisticsWithIsPublicDisableAndUserModeratorTest() {
        GlobalSetting globalSetting = new GlobalSetting();
        globalSetting.setCode("STATISTICS_IS_PUBLIC");
        globalSetting.setValue("NO");
        Mockito.when(globalSettingRepository.findGlobalSettingByCode("STATISTICS_IS_PUBLIC")).thenReturn(globalSetting);
        User user = new User();
        user.setIsModerator((byte) 1);
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertEquals(statisticsService.getAllStatistics(principal).getStatusCode(), HttpStatus.OK);
    }
}
