package org.example.theblog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import org.example.theblog.config.ImageConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ImageService {

    private final ImageConfig imageConfig;

    public ResponseEntity<?> postImage(MultipartFile file) {
        Map<String, String> errors = new HashMap<>();

        if (file.isEmpty() || file.getSize() >= 1024 * 1024 * 4) {
            errors.put("image", "Размер файла превышает допустимый размер");
            return new ResponseEntity<>(new ImageResponse(false, errors), HttpStatus.BAD_REQUEST);
        }

        Cloudinary cloudinary = imageConfig.getCloudinary();

        Map<String, String> result = new HashMap<>();
        try {
            result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(result.get("url"));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageResponse(Boolean result, Map<String, String> errors) {

    }
}
