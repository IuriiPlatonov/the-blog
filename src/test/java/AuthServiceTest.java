
import org.example.theblog.exceptions.MultiuserModeException;
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
import org.springframework.security.authentication.AuthenticationManager;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

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
        assertFalse(authService.getAuth(null).result());
    }

    @Test
    @DisplayName("Get Auth, the Principal is not null")
    public void getAuthTest() {
        Mockito.when(principal.getName()).thenReturn(email);
        Mockito.when(userRepository.findByEmail(principal.getName())).thenReturn(Optional.of(user));
        System.out.println(principal);
        assertTrue(authService.getAuth(principal).result());
    }

    @Test
    @DisplayName("Register, registration is not allowed")
    public void registerUserWithMultiuserModeIsFalseTest() {
        AuthService.RegisterRequest registerRequest = Mockito.mock(AuthService.RegisterRequest.class);
        GlobalSetting globalSetting = Mockito.mock(GlobalSetting.class);

        Mockito.when(globalSetting.getValue()).thenReturn("NO");
        Mockito.when(globalSettingRepository.findGlobalSettingByCode("MULTIUSER_MODE")).thenReturn(globalSetting);
        assertThrows(MultiuserModeException.class, () -> authService.register(registerRequest));
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
                thenReturn(Optional.of("qwerty"));
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertEquals(authService.register(registerRequest).errors(),
                Map.of("email", "???????? e-mail ?????? ??????????????????????????????"));
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
                thenReturn(Optional.of("qwerty"));

        assertEquals(authService.register(registerRequest).errors(),
                Map.of("name", "?????? ?????????????? ??????????????"));
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
                thenReturn(Optional.of("qwerty"));

        assertEquals(authService.register(registerRequest).errors(),
                Map.of("name", "?????? ?????????????? ??????????????"));
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
                thenReturn(Optional.empty());

        assertEquals(authService.register(registerRequest).errors(),
                Map.of("captcha", "?????? ?? ???????????????? ???????????? ??????????????"));
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
                thenReturn(Optional.of("qwerty"));

        assertEquals(authService.register(registerRequest).errors(),
                Map.of("password", "???????????? ???????????? 6-???? ????????????????"));
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
                thenReturn(Optional.of("qwerty"));

        assertTrue(authService.register(registerRequest).result());
    }

    @Test
    @DisplayName("Login, wrong email")
    public void loginWithWrongEmailTest() {
        AuthService.LoginRequest loginRequest = Mockito.mock(AuthService.LoginRequest.class);

        Mockito.when(loginRequest.eMail()).thenReturn(email);
        Mockito.when(userRepository.findByEmail(loginRequest.eMail())).thenReturn(Optional.empty());

        assertFalse(authService.login(loginRequest).result());
    }

    @Test
    @DisplayName("Login, wrong password")
    public void loginWithWrongPasswordTest() {
        AuthService.LoginRequest loginRequest = Mockito.mock(AuthService.LoginRequest.class);
        Mockito.when(user.getPassword()).thenReturn("222222");
        Mockito.when(loginRequest.eMail()).thenReturn(email);
        Mockito.when(loginRequest.password()).thenReturn("111111");
        Mockito.when(userRepository.findByEmail(loginRequest.eMail())).thenReturn(Optional.of(user));

        assertFalse(authService.login(loginRequest).result());
    }

    @Test
    @DisplayName("Login is successful")
    public void loginTest() {
        AuthService.LoginRequest loginRequest = Mockito.mock(AuthService.LoginRequest.class);
        Mockito.when(user.getPassword()).thenReturn("$2y$12$q0FLX6yp0w87cJ83vlh6yOJ9H1Ermzcd9pcfn0/cRqcGbV/iSSUl2");
        Mockito.when(loginRequest.eMail()).thenReturn(email);
        Mockito.when(loginRequest.password()).thenReturn("111111");
        Mockito.when(userRepository.findByEmail(loginRequest.eMail())).thenReturn(Optional.of(user));

        assertTrue(authService.login(loginRequest).result());
    }

    @Test
    @DisplayName("Logout is successful")
    public void logoutTest() {
        assertTrue(authService.logout().result());
    }

    @Test
    @DisplayName("Change password, old captcha")
    public void changePasswordWithOldCaptchaTest() {
        AuthService.CodeRequest codeRequest = Mockito.mock(AuthService.CodeRequest.class);
        Mockito.when(codeRequest.captchaSecret()).thenReturn("qwerty");
        Mockito.when(codeRequest.password()).thenReturn("123456");
        Mockito.when(captchaCodeRepository.findCaptchaCodeBySecretCode(codeRequest.captchaSecret())).
                thenReturn(Optional.empty());

        assertEquals(authService.changePassword(codeRequest).errors(),
                Map.of("code", """
                        ???????????? ?????? ???????????????????????????? ???????????? ????????????????.
                        <a href=
                        "/login/restore-password">?????????????????? ???????????? ??????????</a>"""));
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

        assertEquals(authService.changePassword(codeRequest).errors(),
                Map.of("password", "???????????? ???????????? 6-???? ????????????????"));
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

        assertEquals(authService.changePassword(codeRequest).errors(),
                Map.of("captcha", "?????? ?? ???????????????? ???????????? ??????????????"));
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

        assertTrue(authService.changePassword(codeRequest).result());
    }

}
