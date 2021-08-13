package org.example.theblog.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.cage.Cage;
import com.github.cage.image.ConstantColorGenerator;
import com.github.cage.image.EffectConfig;
import com.github.cage.image.Painter;
import com.github.cage.image.ScaleConfig;
import com.github.cage.token.RandomCharacterGeneratorFactory;
import com.github.cage.token.RandomTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.example.theblog.model.entity.CaptchaCode;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.CaptchaCodeRepository;
import org.example.theblog.model.repository.GlobalSettingRepository;
import org.example.theblog.model.repository.PostRepository;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PostRepository postRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final UserRepository userRepository;
    private final GlobalSettingRepository globalSettingRepository;
    @Value("${blog.timeToDeleteCaptchaCodeInMinutes}")
    private int time;

    public ResponseEntity<AuthResponse> getAuth(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new AuthResponse(false, null));
        }
        return ResponseEntity.ok(getAuthResponse(userRepository.findUsersByEmail(principal.getName())));
    }

    public ResponseEntity<CaptchaResponse> generateCaptcha() throws NoSuchAlgorithmException {
        Cage cage = initCage();
        String code = cage.getTokenGenerator().next();
        String secretCode = generateSecretCode(code);
        byte[] captchaPicture = cage.draw(code);
        String encodedCaptchaPicture = Base64.getEncoder().encodeToString(captchaPicture);

        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setTime(LocalDateTime.now());
        captchaCode.setCode(code.toLowerCase());
        captchaCode.setSecretCode(secretCode);
        captchaCodeRepository.save(captchaCode);

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(() -> captchaCodeRepository.deleteCaptchaCodeBySecretCode(secretCode),
                this.time, TimeUnit.MINUTES);

        return ResponseEntity.ok(
                new CaptchaResponse(secretCode, "data:image/png;base64, ".concat(encodedCaptchaPicture)));
    }

    public ResponseEntity<RegisterResponse> register(RegisterRequest request) {

        if (globalSettingRepository.findGlobalSettingByCode("MULTIUSER_MODE").getValue().equals("NO")) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Map<String, String> errors = new HashMap<>();
        Matcher goodName = Pattern.compile(".{3,30}").matcher(request.name());

        if (userRepository.findByEmail(request.eMail()).isPresent()) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }

        if (!goodName.matches()) {
            errors.put("name", "Имя указано неверно");
        }

        if (request.password().length() < 6) {
            errors.put("password", "Пароль короче 6-ти символов");
        }

        if (captchaCodeRepository.findCode(request.captcha().toLowerCase()) == null) {
            errors.put("captcha", "Код с картинки введён неверно");
        }

        if (errors.size() == 0) {
            User newUser = new User();
            newUser.setName(request.name());
            newUser.setIsModerator((byte) 0);
            newUser.setRegTime(LocalDateTime.now());
            newUser.setEmail(request.eMail());
            newUser.setPassword(new BCryptPasswordEncoder(12)
                    .encode(request.password()));
            userRepository.save(newUser);
        }

        return ResponseEntity.ok(new RegisterResponse(errors.size() == 0, errors));
    }

    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.eMail())
                .orElse(null);

        boolean isPasswordCorrect = false;

        if (user != null) {
            isPasswordCorrect = new BCryptPasswordEncoder(12)
                    .matches(request.password(), user.getPassword());
        }

        if (isPasswordCorrect) {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.eMail(), request.password()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            return ResponseEntity.ok(getAuthResponse(user));

        }

        return ResponseEntity.ok(getAuthResponse(null));
    }

    public ResponseEntity<AuthResponse> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new AuthResponse(true, null));
    }

    public ResponseEntity<RegisterResponse> changePassword(CodeRequest request) {
        Map<String, String> errors = new HashMap<>();

        CaptchaCode captchaCode = captchaCodeRepository.findCaptchaCodeBySecretCode(request.captchaSecret())
                .orElse(null);

        if (captchaCode == null) {
            errors.put("code", """
                    Ссылка для восстановления пароля устарела.
                    <a href=
                    "/login/restore-password">Запросить ссылку снова</a>""");
        }

        if (request.password().length() < 6) {
            errors.put("password", "Пароль короче 6-ти символов");
        }

        if (captchaCode != null && !request.captcha().equals(captchaCode.getCode())) {
            errors.put("captcha", "Код с картинки введён неверно");
        }

        if (errors.size() == 0) {
            User user = userRepository.findUsersByCode(request.code());
            if (request.code().equals(user.getCode())) {
                user.setPassword(new BCryptPasswordEncoder(12)
                        .encode(request.password()));
                userRepository.flush();
            }
        }

        return ResponseEntity.ok(new RegisterResponse(errors.size() == 0, errors));
    }

    private AuthResponse getAuthResponse(User user) {
        return user != null
                ? new AuthResponse(true,
                new AuthorizedUser(
                        user.getId(),
                        user.getName(),
                        user.getPhoto(),
                        user.getEmail(),
                        user.getIsModerator() == 1,
                        user.getIsModerator() == 1 ? postRepository.getPostCountByStatusNew() : 0,
                        user.getIsModerator() == 1))
                : new AuthResponse(false, null);
    }

    private String generateSecretCode(String code) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(code.getBytes());
        return DatatypeConverter
                .printHexBinary(md.digest()).toUpperCase();
    }

    private Cage initCage() {

        final int HEIGHT = 35;
        final int WIDTH = 100;

        char[] charArray = {'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м',
                'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'э', 'ю',
                'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н',
                'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Э', 'Ю', 'Я'};

        Random rnd = new Random();

        return new Cage(new Painter(WIDTH, HEIGHT, null, Painter.Quality.MAX,
                new EffectConfig(false, false, false, true,
                        new ScaleConfig(1F, 1F)), rnd), null,
                new ConstantColorGenerator(Color.BLACK), null, Cage.DEFAULT_COMPRESS_RATIO,
                new RandomTokenGenerator(rnd, new RandomCharacterGeneratorFactory(charArray,
                        null, rnd), 6, 2), rnd);
    }

    record AuthorizedUser(int id, String name, String photo, String email, boolean moderation, int moderationCount,
                          boolean settings) {
    }

    public record CaptchaResponse(String secret, String image) {

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AuthResponse(boolean result, AuthorizedUser user) {
    }

    public record RegisterRequest(@JsonProperty("e_mail") String eMail, String password, String name, String captcha,
                                  @JsonProperty("captcha_secret") String captchaSecret) {

    }

    public record CodeRequest(String code, String password, String captcha,
                              @JsonProperty("captcha_secret") String captchaSecret) {

    }

    public record RegisterResponse(boolean result, Map<String, String> errors) {

    }

    public record LoginRequest(@JsonProperty("e_mail") String eMail, String password) {
    }
}
