package org.example.theblog.controllers;

import lombok.RequiredArgsConstructor;
import org.example.theblog.exceptions.MultiuserModeException;
import org.example.theblog.service.AuthService;
import org.example.theblog.service.MailService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<AuthService.AuthResponse> settings(Principal principal) {
        return ResponseEntity.ok(authService.getAuth(principal));
    }

    @GetMapping("/captcha")
    public ResponseEntity<AuthService.CaptchaResponse> getCaptcha() throws NoSuchAlgorithmException {
        return ResponseEntity.ok(authService.generateCaptcha());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthService.RegisterResponse> register(@RequestBody AuthService.RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (MultiuserModeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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

    @PostMapping("/password")
    public ResponseEntity<AuthService.RegisterResponse> changePassword(@RequestBody AuthService.CodeRequest request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }
}
