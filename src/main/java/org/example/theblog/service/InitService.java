package org.example.theblog.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Data
@Component
public class InitService {
    @Value("${blog.title}")
    private String title;
    @Value("${blog.subtitle}")
    private String subtitle;
    @Value("${blog.phone}")
    private String phone;
    @Value("${blog.email}")
    private String email;
    @Value("${blog.copyright}")
    private String copyright;
    @Value("${blog.copyrightFrom}")
    private String copyrightFrom;

    public ResponseEntity<InitResponse> init(){
        return ResponseEntity.ok(new InitResponse(title, subtitle, phone, email, copyright, copyrightFrom));
    }

    public record InitResponse(String title, String subtitle, String phone,
                               String email, String copyright, String copyrightFrom){

    }
}
