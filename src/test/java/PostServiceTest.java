import org.example.theblog.model.entity.GlobalSetting;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.*;
import org.example.theblog.service.PostService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class PostServiceTest {
    private static PostService postService;
    private static final PostService.PostRequest request = Mockito.mock(PostService.PostRequest.class);
    private static final PostRepository postRepository = Mockito.mock(PostRepository.class);
    private static final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private static final TagRepository tagRepository = Mockito.mock(TagRepository.class);
    private static final PostVoteRepository postVoteRepository = Mockito.mock(PostVoteRepository.class);
    private static final GlobalSettingRepository globalSettingRepository = Mockito.mock(GlobalSettingRepository.class);
    private static final Principal principal = Mockito.mock(Principal.class);
    private static GlobalSetting globalSetting;


    @BeforeAll
    static void beforeAll() {
        postService = new PostService(
                postRepository, userRepository, tagRepository, postVoteRepository, globalSettingRepository);

        globalSetting = new GlobalSetting();
        globalSetting.setValue("YES");
    }

    static void initGlobalSetting() {
        Mockito.when(request.title()).thenReturn("testTitle");
        Mockito.when(request.timestamp()).thenReturn(1L);
        Mockito.when(request.active()).thenReturn((byte) 1);
        Mockito.when(request.text()).thenReturn("the test text is more than 50 characters, " +
                                                "the test text is more than 50 characters");
        Mockito.when(request.tags()).thenReturn(List.of("test"));

    }

    @Test
    @DisplayName("Write post is successful")
    public void writePostTest() {
        initGlobalSetting();

        Mockito.when(globalSettingRepository.findGlobalSettingByCode(any())).thenReturn(globalSetting);
        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());
        Mockito.when(postRepository.save(any())).thenReturn(null);
        assertTrue(Objects.requireNonNull(postService.writePost(request, principal).getBody(),
                "In the writePostTest, the assertTrue parameter is null").result());
    }

    @Test
    @DisplayName("Write post, title is empty")
    public void writePostWithoutTitleTest() {
        initGlobalSetting();

        Mockito.when(request.title()).thenReturn(" ");
        Mockito.when(globalSettingRepository.findGlobalSettingByCode(any())).thenReturn(globalSetting);
        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());
        Mockito.when(postRepository.save(any())).thenReturn(null);
        assertEquals(Objects.requireNonNull(postService.writePost(request, principal).getBody(),
                        "In the writePostWithoutTitleTest, the assertEquals parameter is null")
                .errors(), Map.of("title", "Заголовок не установлен"));
    }

    @Test
    @DisplayName("Write post, the title is short")
    public void writePostWithShortTitleTest() {
        initGlobalSetting();

        Mockito.when(request.title()).thenReturn("qw");
        Mockito.when(globalSettingRepository.findGlobalSettingByCode(any())).thenReturn(globalSetting);
        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());
        Mockito.when(postRepository.save(any())).thenReturn(null);
        assertEquals(Objects.requireNonNull(postService.writePost(request, principal).getBody(),
                        "In the writePostWithShortTitleTest, the assertEquals parameter is null")
                .errors(), Map.of("title", "Заголовок слишком короткий"));
    }

    @Test
    @DisplayName("Write post, the text is empty")
    public void writePostWithEmptyTextTest() {
        initGlobalSetting();

        Mockito.when(request.text()).thenReturn(" ");
        Mockito.when(globalSettingRepository.findGlobalSettingByCode(any())).thenReturn(globalSetting);
        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());
        Mockito.when(postRepository.save(any())).thenReturn(null);
        assertEquals(Objects.requireNonNull(postService.writePost(request, principal).getBody(),
                        "In the writePostWithEmptyTextTest, the assertEquals parameter is null")
                .errors(), Map.of("text", "Текст публикации не установлен"));
    }

    @Test
    @DisplayName("Write post, the text is short")
    public void writePostWithShortTextTest() {
        initGlobalSetting();

        Mockito.when(request.text()).thenReturn("testText");
        Mockito.when(globalSettingRepository.findGlobalSettingByCode(any())).thenReturn(globalSetting);
        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());
        Mockito.when(postRepository.save(any())).thenReturn(null);
        assertEquals(Objects.requireNonNull(postService.writePost(request, principal).getBody(),
                        "In the writePostWithShortTextTest, the assertEquals parameter is null")
                .errors(), Map.of("text", "Текст публикации слишком короткий"));
    }

}
