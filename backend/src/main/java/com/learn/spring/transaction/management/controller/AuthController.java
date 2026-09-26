package com.learn.spring.transaction.management.controller;

import com.learn.spring.transaction.management.dto.LoginRequest;
import com.learn.spring.transaction.management.dto.LoginResponse;
import com.learn.spring.transaction.management.dto.RegisterRequest;
import com.learn.spring.transaction.management.dto.UserResponse;
import com.learn.spring.transaction.management.service.AuthService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        UserResponse response = this.authService.register(request);
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }


}
