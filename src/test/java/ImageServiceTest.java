import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.example.theblog.config.ImageConfig;
import org.example.theblog.service.ImageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ImageServiceTest {

    private static ImageService imageService;
    static ImageConfig imageConfig = Mockito.mock(ImageConfig.class);
    MultipartFile file = Mockito.mock(MultipartFile.class);
    Uploader uploader = Mockito.mock(Uploader.class);
    Cloudinary cloudinary = Mockito.mock(Cloudinary.class);

    @BeforeAll
    static void beforeAll() {
        imageService = new ImageService(imageConfig);
    }

    @Test
    @DisplayName("Post image is successful")
    public void postImageTest() {
        Mockito.when(file.getSize()).thenReturn(1L);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(imageConfig.getCloudinary()).thenReturn(cloudinary);
        Mockito.when(cloudinary.uploader()).thenReturn(uploader);

        assertNull(imageService.postImage(file));
    }
}
