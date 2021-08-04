package org.example.theblog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ImageService {

    public static ImageResponse postImage(MultipartFile file) {
        Map<String, String> errors = new HashMap<>();

        if (file.isEmpty() || file.getSize() >= 1024 * 1024 * 4) {
            errors.put("image", "Размер файла превышает допустимый размер");
            return new ImageResponse(null, false, errors);
        }

        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcqba34wa",
                "api_key", "654122142294935",
                "api_secret", "_6XtFAbZ8mY37WSu6Tf2v6cgnPw"));

        Map<String, String> result = new HashMap<>();
        try {
            result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ImageResponse(result.get("url"), null, null);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageResponse(String filePath, Boolean result, Map<String, String> errors) {

    }
}
