package org.example.theblog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.example.theblog.config.ImageConfig;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ImageConfig imageConfig;
    private final UserRepository userRepository;

    public ResponseEntity<ProfileResponse> editProfileWithoutPhoto(ProfileRequest request, Principal principal) {
        return ResponseEntity.ok(editProfile(
                null,
                request.removePhoto(),
                request.name(),
                request.email(),
                request.password(),
                principal));
    }

    public ResponseEntity<ProfileResponse> editProfileWithPhoto(MultipartFile photo, int removePhoto, String name, String email, String password, Principal principal) {
        return ResponseEntity.ok(editProfile(photo,
                removePhoto,
                name,
                email,
                password,
                principal));
    }

    private ProfileResponse editProfile(MultipartFile photo, int removePhoto, String name,
                                        String email, String password, Principal principal) {
        Map<String, String> errors = new HashMap<>();

        User user = userRepository.findUsersByEmail(principal.getName());

        if (Objects.nonNull(photo)) {
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
        }

        if (removePhoto == 1) {
            user.setPhoto("");
        }

        if (Objects.nonNull(password) && (password.length() < 6 || password.isBlank())) {
            errors.put("password", "Пароль короче 6 символов");
        }

        if (Objects.nonNull(password) && password.length() >= 6 && !password.isBlank()) {
            user.setPassword(new BCryptPasswordEncoder(12)
                    .encode(password));
        }

        if (Objects.nonNull(email)
            && userRepository.findByEmail(email).isPresent()
            && !principal.getName().equals(email)) {
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }

        if (Objects.nonNull(email)) {
            user.setEmail(email);
        }

        if (Objects.nonNull(email)) {
            boolean badName = !name.matches(".{3,30}");
            if (badName) {
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
