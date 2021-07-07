package org.example.theblog.controllers;

import org.example.theblog.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    AuthService authService;

    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/check")
    private AuthService.AuthResponse settings() {
        return authService.getAuth();
    }

    @GetMapping("/captcha")
    private AuthService.CaptchaResponse getCaptcha() throws NoSuchAlgorithmException {
        return authService.generateCaptcha();
    }

    @PostMapping("/register")
    private AuthService.RegisterResponse register(@RequestBody AuthService.RegisterRequest request) {
        return authService.register(request);
    }
}
