package org.example.theblog.controllers;

import org.example.theblog.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;


    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/check")
    private AuthService.AuthResponse settings(Principal principal) {
        return authService.getAuth(principal);
    }

    @GetMapping("/captcha")
    private AuthService.CaptchaResponse getCaptcha() throws NoSuchAlgorithmException {
        return authService.generateCaptcha();
    }

    @PostMapping("/register")
    private AuthService.RegisterResponse register(@RequestBody AuthService.RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    private ResponseEntity<AuthService.AuthResponse> login(@RequestBody AuthService.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
