package org.example.theblog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.example.theblog.config.ImageConfig;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ImageConfig imageConfig;
    private final UserRepository userRepository;

    public ProfileResponse editProfileWithoutPhoto(ProfileRequest request, Principal principal) {
        return editProfile(
                null,
                request.removePhoto(),
                request.name(),
                request.email(),
                request.password(),
                principal);
    }

    public ProfileResponse editProfileWithPhoto(MultipartFile photo, int removePhoto, String name, String email, String password, Principal principal) {
        return editProfile(photo,
                removePhoto,
                name,
                email,
                password,
                principal);
    }

    private ProfileResponse editProfile(MultipartFile photo, int removePhoto, String name, String email, String password, Principal principal) {
        Map<String, String> errors = new HashMap<>();

        User user = userRepository.findUsersByEmail(principal.getName());

        if (photo != null) {
            if (photo.isEmpty() || photo.getSize() >= 1024 * 1024 * 5) {
                errors.put("photo", "Размер файла превышает допустимый размер");
                return new ProfileResponse(false, errors);
            }

            Cloudinary cloudinary = imageConfig.getCloudinary();

            Map<String, String> result = new HashMap<>();
            try {
                result = cloudinary.uploader().upload(photo.getBytes(),
                        ObjectUtils.emptyMap());
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setPhoto(result.get("url"));
        } else {
            if (removePhoto == 1) {
                user.setPhoto("");
            }
        }

        if (password != null && password.length() < 6) {
            errors.put("password", "Пароль короче 6 символов");
        }

        if (password != null) {
            user.setPassword(new BCryptPasswordEncoder(12)
                    .encode(password));
        }

        if (email != null
            && userRepository.findByEmail(email).isPresent()
            && !principal.getName().equals(email)) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }

        if (email != null) {
            user.setEmail(email);
        }

        if (name != null) {
            Matcher badName = Pattern.compile("\\w").matcher(name);
            if (badName.find()) {
                errors.put("name", "Имя указано неверно");
            } else {
                user.setName(name);
            }
        }

        if (errors.size() == 0) {
            userRepository.flush();
        }

        return new ProfileResponse(errors.size() == 0, errors);
    }

    public record ProfileResponse(boolean result, Map<String, String> errors) {
    }

    public record ProfileRequest(String photo, int removePhoto, String name, String email, String password) {
    }
}
