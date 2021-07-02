package org.example.theblog.controllers;

import org.example.theblog.api.response.AuthResponse;
import org.example.theblog.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    AuthService authService;

    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/check")
    private AuthResponse settings(){
        return authService.getAuth();
    }
}
