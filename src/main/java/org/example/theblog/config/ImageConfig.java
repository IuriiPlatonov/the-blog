package org.example.theblog.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class ImageConfig {

    public Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcqba34wa",
                "api_key", "654122142294935",
                "api_secret", "_6XtFAbZ8mY37WSu6Tf2v6cgnPw"));
    }
}
