package com.lucasrenan.taskmanager.controller;

import com.lucasrenan.taskmanager.dto.request.LoginRequest;
import com.lucasrenan.taskmanager.dto.request.RegisterRequest;
import com.lucasrenan.taskmanager.dto.response.LoginResponse;
import com.lucasrenan.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}