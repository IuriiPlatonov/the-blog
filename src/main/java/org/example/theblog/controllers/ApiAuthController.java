package org.example.theblog.controllers;

import lombok.RequiredArgsConstructor;
import org.example.theblog.service.AuthService;
import org.example.theblog.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;
    private final MailService mailService;

    @GetMapping("/check")
    public AuthService.AuthResponse settings(Principal principal) {
        return authService.getAuth(principal);
    }

    @GetMapping("/captcha")
    public AuthService.CaptchaResponse getCaptcha() throws NoSuchAlgorithmException {
        return authService.generateCaptcha();
    }

    @PostMapping("/register")
    public AuthService.RegisterResponse register(@RequestBody AuthService.RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthService.AuthResponse> login(@RequestBody AuthService.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/logout")
    public ResponseEntity<AuthService.AuthResponse> logout() {
        return ResponseEntity.ok(authService.logout());
    }

    @PostMapping("/restore")
    public ResponseEntity<MailService.MailResponse> restore(@RequestBody MailService.MailRequest request) {
        return ResponseEntity.ok(mailService.restore(request));
    }

    //    POST /api/auth/password
    @PostMapping("/password")
    public ResponseEntity<AuthService.RegisterResponse> password(@RequestBody AuthService.CodeRequest request) {
        return ResponseEntity.ok(authService.password(request));
    }
}
