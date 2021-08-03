package org.example.theblog.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        String originalFilename = file.getOriginalFilename();
        String name = null;
        if (originalFilename != null) {
            String[] paths = originalFilename.split("\\.");
            Path filepath = Paths.get("upload", originalFilename.hashCode()
                                                + "." + paths[paths.length - 1]);
            name = filepath.toString();
            errors.put(filepath.toString(), "");
            try {
                file.transferTo(filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ImageResponse(name, null, null);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageResponse(String filePath, Boolean result, Map<String, String> errors) {

    }
}
