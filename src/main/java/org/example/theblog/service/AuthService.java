package org.example.theblog.service;

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
import org.example.theblog.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private final CaptchaCodeRepository captchaCodeRepository;
    private final UserRepository userRepository;
    @Value("${blog.timeToDeleteCaptchaCodeInMinutes}")
    private int time;

    public AuthResponse getAuth() {
        return new AuthResponse(false, null);
    }

    public CaptchaResponse generateCaptcha() throws NoSuchAlgorithmException {
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

        return new CaptchaResponse(secretCode, "data:image/png;base64, ".concat(encodedCaptchaPicture));
    }

    public RegisterResponse register(RegisterRequest request) {

        Map<String, String> errors = new HashMap<>();
        Matcher badName = Pattern.compile("\\w").matcher(request.name());

        if (userRepository.findUsersByEmail(request.eMail()) != null) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }

        if (badName.find()) {
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
            newUser.setPassword(request.password());
            userRepository.save(newUser);
        }

        return new RegisterResponse(errors.size() == 0, errors);
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

    public record AuthResponse(boolean result, AuthorizedUser user) {
    }

    public record RegisterRequest(@JsonProperty("e_mail") String eMail, String password, String name, String captcha,
                                  @JsonProperty("captcha_secret") String captchaSecret) {

    }

    public record RegisterResponse(boolean result, /*@JsonAnyGetter*/ Map<String, String> errors) {

    }
}
