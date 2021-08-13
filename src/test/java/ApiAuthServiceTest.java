
import org.example.theblog.model.entity.CaptchaCode;
import org.example.theblog.model.entity.GlobalSetting;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.CaptchaCodeRepository;
import org.example.theblog.model.repository.GlobalSettingRepository;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.UserRepository;
import org.example.theblog.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ApiAuthServiceTest {

    AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
    PostRepository postRepository = Mockito.mock(PostRepository.class);
    CaptchaCodeRepository captchaCodeRepository = Mockito.mock(CaptchaCodeRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    GlobalSettingRepository globalSettingRepository = Mockito.mock(GlobalSettingRepository.class);
    Principal principal = Mockito.mock((Principal.class));
    User user = Mockito.mock(User.class);
    AuthService authService;

    private final String email = "test@test.com";

    {
        authService = new AuthService(authenticationManager, postRepository, captchaCodeRepository,
                userRepository, globalSettingRepository);
    }

    @Test
    @DisplayName("Get Auth, the Principal is null")
    public void getAuthTestWhenPrincipalNull() {
        assertFalse(Objects.requireNonNull(authService.getAuth(null).getBody()).result(),
                "In the getAuthTestWhenPrincipalNull, the assertFalse parameter is null");
    }

    @Test
    @DisplayName("Get Auth, the Principal is not null")
    public void getAuthTest() {
        Mockito.when(principal.getName()).thenReturn(email);
        Mockito.when(userRepository.findUsersByEmail(principal.getName())).thenReturn(user);

        assertTrue(Objects.requireNonNull(authService.getAuth(principal).getBody()).result(),
                "In the getAuthTest, the assertTrue parameter is null");
    }

    @Test
    @DisplayName("Register, registration is not allowed")
    public void registerUserWithMultiuserModeIsFalseTest() {
        AuthService.RegisterRequest registerRequest = Mockito.mock(AuthService.RegisterRequest.class);
        GlobalSetting globalSetting = Mockito.mock(GlobalSetting.class);

        Mockito.when(globalSetting.getValue()).thenReturn("NO");
        Mockito.when(globalSettingRepository.findGlobalSettingByCode("MULTIUSER_MODE")).thenReturn(globalSetting);

        assertEquals(authService.register(registerRequest).getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Register, email address is exist")
    public void registerUserWithEmailDuplicateTest() {
        AuthService.RegisterRequest registerRequest = Mockito.mock(AuthService.RegisterRequest.class);
        Mockito.when(registerRequest.name()).thenReturn("Tom");
        Mockito.when(registerRequest.captcha()).thenReturn("qwerty");
        Mockito.when(registerRequest.password()).thenReturn("111111");
        Mockito.when((registerRequest.eMail())).thenReturn(email);

        GlobalSetting globalSetting = Mockito.mock(GlobalSetting.class);
        Mockito.when(globalSetting.getValue()).thenReturn("YES");

        Mockito.when(globalSettingRepository.findGlobalSettingByCode("MULTIUSER_MODE")).thenReturn(globalSetting);
        Mockito.when(captchaCodeRepository.findCode(registerRequest.captcha())).
                thenReturn(String.valueOf(Optional.of("qwerty")));
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertEquals(Objects.requireNonNull(authService.register(registerRequest).getBody(),
                        "In the registerUserWithEmailDuplicateTest, the assertEquals 1st parameter is null").
                errors(), Map.of("email", "Этот e-mail уже зарегистрирован"));
    }

    @Test
    @DisplayName("Register, name is too small")
    public void registerNameIsSmallTest() {
        AuthService.RegisterRequest registerRequest = Mockito.mock(AuthService.RegisterRequest.class);
        Mockito.when(registerRequest.name()).thenReturn("To");
        Mockito.when(registerRequest.captcha()).thenReturn("qwerty");
        Mockito.when(registerRequest.password()).thenReturn("111111");

        GlobalSetting globalSetting = Mockito.mock(GlobalSetting.class);
        Mockito.when(globalSetting.getValue()).thenReturn("YES");

        Mockito.when(globalSettingRepository.findGlobalSettingByCode("MULTIUSER_MODE")).thenReturn(globalSetting);

        Mockito.when(captchaCodeRepository.findCode(registerRequest.captcha())).
                thenReturn(String.valueOf(Optional.of("qwerty")));

        assertEquals(Objects.requireNonNull(authService.register(registerRequest).getBody(),
                        "In the registerNameIsSmallTest, the assertEquals 1st parameter is null").errors(),
                Map.of("name", "Имя указано неверно"));
    }

    @Test
    @DisplayName("Register, name is too long")
    public void registerNameIsLongTest() {
        AuthService.RegisterRequest registerRequest = Mockito.mock(AuthService.RegisterRequest.class);
        Mockito.when(registerRequest.name()).thenReturn("Tom Test Tom Test Tom Test Tom Test");
        Mockito.when(registerRequest.captcha()).thenReturn("qwerty");
        Mockito.when(registerRequest.password()).thenReturn("111111");

        GlobalSetting globalSetting = Mockito.mock(GlobalSetting.class);
        Mockito.when(globalSetting.getValue()).thenReturn("YES");

        Mockito.when(globalSettingRepository.findGlobalSettingByCode("MULTIUSER_MODE")).thenReturn(globalSetting);

        Mockito.when(captchaCodeRepository.findCode(registerRequest.captcha())).
                thenReturn(String.valueOf(Optional.of("qwerty")));

        assertEquals(Objects.requireNonNull(authService.register(registerRequest).getBody(),
                        "In the registerNameIsLongTest, the assertEquals 1st parameter is null").errors(),
                Map.of("name", "Имя указано неверно"));
    }

    @Test
    @DisplayName("Register, user with an incorrect captcha")
    public void registerUserWithBadCaptchaTest() {
        AuthService.RegisterRequest registerRequest = Mockito.mock(AuthService.RegisterRequest.class);
        Mockito.when(registerRequest.name()).thenReturn("Tom");
        Mockito.when(registerRequest.captcha()).thenReturn("qwerty");
        Mockito.when(registerRequest.password()).thenReturn("111111");

        GlobalSetting globalSetting = Mockito.mock(GlobalSetting.class);
        Mockito.when(globalSetting.getValue()).thenReturn("YES");

        Mockito.when(globalSettingRepository.findGlobalSettingByCode("MULTIUSER_MODE")).thenReturn(globalSetting);

        Mockito.when(captchaCodeRepository.findCode(registerRequest.captcha())).
                thenReturn(null);

        assertEquals(Objects.requireNonNull(authService.register(registerRequest).getBody(),
                        "In the registerUserWithBadCaptchaTest, the assertEquals 1st parameter is null").
                errors(), Map.of("captcha", "Код с картинки введён неверно"));
    }

    @Test
    @DisplayName("Register, password shorter than 6 characters")
    public void registerUserWithSmallPasswordTest() {
        AuthService.RegisterRequest registerRequest = Mockito.mock(AuthService.RegisterRequest.class);
        Mockito.when(registerRequest.name()).thenReturn("Tom");
        Mockito.when(registerRequest.captcha()).thenReturn("qwerty");
        Mockito.when(registerRequest.password()).thenReturn("12345");

        GlobalSetting globalSetting = Mockito.mock(GlobalSetting.class);
        Mockito.when(globalSetting.getValue()).thenReturn("YES");

        Mockito.when(globalSettingRepository.findGlobalSettingByCode("MULTIUSER_MODE")).thenReturn(globalSetting);

        Mockito.when(captchaCodeRepository.findCode(registerRequest.captcha())).
                thenReturn(String.valueOf(Optional.of("qwerty")));

        assertEquals(Objects.requireNonNull(authService.register(registerRequest).getBody(),
                        "In the registerUserWithSmallPasswordTest, the assertEquals 1st parameter is null").
                errors(), Map.of("password", "Пароль короче 6-ти символов"));
    }

    @Test
    @DisplayName("Registration is successful")
    public void registerTest() {
        AuthService.RegisterRequest registerRequest = Mockito.mock(AuthService.RegisterRequest.class);
        Mockito.when(registerRequest.name()).thenReturn("Tom");
        Mockito.when(registerRequest.captcha()).thenReturn("qwerty");
        Mockito.when(registerRequest.password()).thenReturn("123456");

        GlobalSetting globalSetting = Mockito.mock(GlobalSetting.class);
        Mockito.when(globalSetting.getValue()).thenReturn("YES");

        Mockito.when(globalSettingRepository.findGlobalSettingByCode("MULTIUSER_MODE")).thenReturn(globalSetting);

        Mockito.when(captchaCodeRepository.findCode(registerRequest.captcha())).
                thenReturn(String.valueOf(Optional.of("qwerty")));

        assertTrue(Objects.requireNonNull(authService.register(registerRequest).getBody(),
                        "In the registerUserWithSmallPasswordTest, the assertEquals 1st parameter is null").
                result());
    }

    @Test
    @DisplayName("Login, wrong email")
    public void loginWithWrongEmailTest() {
        AuthService.LoginRequest loginRequest = Mockito.mock(AuthService.LoginRequest.class);

        Mockito.when(loginRequest.eMail()).thenReturn(email);
        Mockito.when(userRepository.findByEmail(loginRequest.eMail())).thenReturn(Optional.empty());

        assertFalse(Objects.requireNonNull(authService.login(loginRequest).getBody(),
                "In the loginWithWrongEmailTest, the assertFalse is null").result());
    }

    @Test
    @DisplayName("Login, wrong password")
    public void loginWithWrongPasswordTest() {
        AuthService.LoginRequest loginRequest = Mockito.mock(AuthService.LoginRequest.class);
        Mockito.when(user.getPassword()).thenReturn("222222");
        Mockito.when(loginRequest.eMail()).thenReturn(email);
        Mockito.when(loginRequest.password()).thenReturn("111111");
        Mockito.when(userRepository.findByEmail(loginRequest.eMail())).thenReturn(Optional.of(user));

        assertFalse(Objects.requireNonNull(authService.login(loginRequest).getBody(),
                "In the loginWithWrongPasswordTest, the assertFalse is null").result());
    }

    @Test
    @DisplayName("Login is successful")
    public void loginTest() {
        AuthService.LoginRequest loginRequest = Mockito.mock(AuthService.LoginRequest.class);
        Mockito.when(user.getPassword()).thenReturn("$2y$12$q0FLX6yp0w87cJ83vlh6yOJ9H1Ermzcd9pcfn0/cRqcGbV/iSSUl2");
        Mockito.when(loginRequest.eMail()).thenReturn(email);
        Mockito.when(loginRequest.password()).thenReturn("111111");
        Mockito.when(userRepository.findByEmail(loginRequest.eMail())).thenReturn(Optional.of(user));

        assertTrue(Objects.requireNonNull(authService.login(loginRequest).getBody(),
                "In the loginTest, the assertTrue is null").result());
    }

    @Test
    @DisplayName("Logout is successful")
    public void logoutTest() {

        assertTrue(Objects.requireNonNull(authService.logout().getBody(),
                "In the logoutTest, the assertTrue is null").result());
    }

    @Test
    @DisplayName("Change password, old captcha")
    public void changePasswordWithOldCaptchaTest() {
        AuthService.CodeRequest codeRequest = Mockito.mock(AuthService.CodeRequest.class);
        Mockito.when(codeRequest.captchaSecret()).thenReturn("qwerty");
        Mockito.when(codeRequest.password()).thenReturn("123456");
        Mockito.when(captchaCodeRepository.findCaptchaCodeBySecretCode(codeRequest.captchaSecret())).
                thenReturn(Optional.empty());

        assertEquals(Objects.requireNonNull(authService.changePassword(codeRequest).getBody(),
                        "In the changePasswordWithOldCaptchaTest, the assertEquals 1st parameter is null").errors(),
                Map.of("code", """
                        Ссылка для восстановления пароля устарела.
                        <a href=
                        "/login/restore-password">Запросить ссылку снова</a>"""));
    }

    @Test
    @DisplayName("Changing password, password is too short")
    public void changePasswordIsTooShortTest() {
        CaptchaCode captchaCode = Mockito.mock(CaptchaCode.class);
        Mockito.when(captchaCode.getCode()).thenReturn("hello");

        AuthService.CodeRequest codeRequest = Mockito.mock(AuthService.CodeRequest.class);
        Mockito.when(codeRequest.captchaSecret()).thenReturn("qwerty");
        Mockito.when(codeRequest.password()).thenReturn("12345");
        Mockito.when(codeRequest.captcha()).thenReturn("hello");
        Mockito.when(captchaCodeRepository.findCaptchaCodeBySecretCode(codeRequest.captchaSecret())).
                thenReturn(Optional.of(captchaCode));

        assertEquals(Objects.requireNonNull(authService.changePassword(codeRequest).getBody(),
                        "In the changePasswordIsTooShortTest, the assertEquals 1st parameter is null").errors(),
                Map.of("password", "Пароль короче 6-ти символов"));
    }

    @Test
    @DisplayName("Changing password, wrong captcha code")
    public void changePasswordWrongCaptchaTest() {
        CaptchaCode captchaCode = Mockito.mock(CaptchaCode.class);
        Mockito.when(captchaCode.getCode()).thenReturn("no hello");

        AuthService.CodeRequest codeRequest = Mockito.mock(AuthService.CodeRequest.class);
        Mockito.when(codeRequest.captchaSecret()).thenReturn("qwerty");
        Mockito.when(codeRequest.password()).thenReturn("123456");
        Mockito.when(codeRequest.captcha()).thenReturn("hello");
        Mockito.when(captchaCodeRepository.findCaptchaCodeBySecretCode(codeRequest.captchaSecret())).
                thenReturn(Optional.of(captchaCode));

        assertEquals(Objects.requireNonNull(authService.changePassword(codeRequest).getBody(),
                        "In the changePasswordWrongCaptchaTest, the assertEquals 1st parameter is null").
                errors(), Map.of("captcha", "Код с картинки введён неверно"));
    }

    @Test
    @DisplayName("Changing password is successful")
    public void changePasswordTest() {
        CaptchaCode captchaCode = Mockito.mock(CaptchaCode.class);
        Mockito.when(captchaCode.getCode()).thenReturn("hello");

        AuthService.CodeRequest codeRequest = Mockito.mock(AuthService.CodeRequest.class);
        Mockito.when(codeRequest.code()).thenReturn("code");
        Mockito.when(codeRequest.captchaSecret()).thenReturn("qwerty");
        Mockito.when(codeRequest.password()).thenReturn("123456");
        Mockito.when(codeRequest.captcha()).thenReturn("hello");
        Mockito.when(captchaCodeRepository.findCaptchaCodeBySecretCode(codeRequest.captchaSecret())).
                thenReturn(Optional.of(captchaCode));
        Mockito.when(user.getCode()).thenReturn("code");
        Mockito.when(userRepository.findUsersByCode(codeRequest.code())).thenReturn(user);

        assertTrue(Objects.requireNonNull(authService.changePassword(codeRequest).getBody(),
                "In the changePasswordTest, the assertTrue is null").result());
    }

}
