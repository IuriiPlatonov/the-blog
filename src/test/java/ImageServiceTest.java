import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.example.theblog.config.ImageConfig;
import org.example.theblog.service.ImageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals(imageService.postImage(file).getStatusCode(), HttpStatus.OK);
    }

    @Test
    @DisplayName("Post image, the file size is too large ")
    public void postImageLargeFileTest() {
        Mockito.when(file.getSize()).thenReturn(1024 * 1024 * 5L);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(imageConfig.getCloudinary()).thenReturn(cloudinary);
        Mockito.when(cloudinary.uploader()).thenReturn(uploader);

        assertEquals(imageService.postImage(file).getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Post image, the file is empty ")
    public void postImageSmallFileTest() {
        Mockito.when(file.getSize()).thenReturn(1024 * 1024 * 3L);
        Mockito.when(file.isEmpty()).thenReturn(true);
        Mockito.when(imageConfig.getCloudinary()).thenReturn(cloudinary);
        Mockito.when(cloudinary.uploader()).thenReturn(uploader);

        assertEquals(imageService.postImage(file).getStatusCode(), HttpStatus.BAD_REQUEST);
    }
}
