import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.example.theblog.config.ImageConfig;
import org.example.theblog.model.entity.User;
import org.example.theblog.model.repository.UserRepository;
import org.example.theblog.service.ProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class ProfileServiceTest {

    ImageConfig imageConfig = Mockito.mock(ImageConfig.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    Principal principal = Mockito.mock(Principal.class);
    ProfileService profileService = new ProfileService(imageConfig, userRepository);

    @Test
    @DisplayName("Edit profile without photo is successful")
    public void editProfileWithoutPhotoTest() {
        ProfileService.ProfileRequest request = new ProfileService.ProfileRequest("", 0,
                "Tom", "test@test.com", "111111");
        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());

        assertTrue(Objects.requireNonNull(profileService.editProfileWithoutPhoto(request, principal).getBody(),
                "In the editProfileWithoutPhotoTest, the assertTrue parameter is null").result());
    }

    @Test
    @DisplayName("Edit profile without photo, password is short")
    public void editProfileWithoutPhotoShortPasswordTest() {
        ProfileService.ProfileRequest request = new ProfileService.ProfileRequest("", 0,
                "Tom", "test@test.com", "    ");
        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());

        assertEquals(Objects.requireNonNull(profileService.editProfileWithoutPhoto(request, principal).getBody(),
                        "In the editProfileWithoutPhotoShortPasswordTest, the assertEquals parameter is null")
                .errors(), Map.of("password", "Пароль короче 6 символов"));
    }

    @Test
    @DisplayName("Edit profile without photo, the email already exists")
    public void editProfileWithoutPhotoExistEmailTest() {
        ProfileService.ProfileRequest request = new ProfileService.ProfileRequest("", 0,
                "Tom", "test@test.com", "111111");
        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());
        Mockito.when(principal.getName()).thenReturn("test");
        Mockito.when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(new User()));

        assertEquals(Objects.requireNonNull(profileService.editProfileWithoutPhoto(request, principal).getBody(),
                        "In the editProfileWithoutPhotoExistEmailTest, the assertEquals parameter is null")
                .errors(), Map.of("email", "Этот e-mail уже зарегистрирован"));
    }


    @Test
    @DisplayName("Edit profile without photo, wrong name")
    public void editProfileWithoutPhotoWrongNameTest() {
        ProfileService.ProfileRequest request = new ProfileService.ProfileRequest("", 0,
                "To", "test@test.com", "111111");
        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());

        assertEquals(Objects.requireNonNull(profileService.editProfileWithoutPhoto(request, principal).getBody(),
                        "In the editProfileWithoutPhotoWrongNameTest, the assertEquals parameter is null")
                .errors(), Map.of("name", "Имя указано неверно"));
    }

    @Test
    @DisplayName("Edit profile with photo, is successful")
    public void editProfileWithPhotoTest() {
        Cloudinary cloudinary = Mockito.mock(Cloudinary.class);
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Uploader uploader = Mockito.mock(Uploader.class);
        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());
        Mockito.when(imageConfig.getCloudinary()).thenReturn(cloudinary);
        Mockito.when(cloudinary.uploader()).thenReturn(uploader);

        assertTrue(Objects.requireNonNull(profileService.editProfileWithPhoto(file, 0, "Tom",
                        "test@test.com", "111111", principal).getBody(),
                "In the editProfileWithPhotoTest, the assertTrue parameter is null").result());
    }

    @Test
    @DisplayName("Edit profile with photo, photo is empty")
    public void editProfileWithEmptyPhotoTest() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.isEmpty()).thenReturn(true);

        Mockito.when(userRepository.findUsersByEmail(any())).thenReturn(new User());

        assertEquals(Objects.requireNonNull(profileService.editProfileWithPhoto(file, 0, "Tom",
                                "test@test.com", "111111", principal).getBody(),
                        "In the editProfileWithEmptyPhotoTest, the assertEquals parameter is null")
                .errors(), Map.of("photo", "Размер файла превышает допустимый размер"));
    }
}
