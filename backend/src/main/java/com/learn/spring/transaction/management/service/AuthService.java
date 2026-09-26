package com.learn.spring.transaction.management.service;

import com.learn.spring.transaction.management.dto.LoginRequest;
import com.learn.spring.transaction.management.dto.LoginResponse;
import com.learn.spring.transaction.management.dto.RegisterRequest;
import com.learn.spring.transaction.management.dto.UserResponse;
import com.learn.spring.transaction.management.entity.AppUser;
import com.learn.spring.transaction.management.entity.Role;
import com.learn.spring.transaction.management.repository.UserRepository;
import com.learn.spring.transaction.management.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager =
                authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request){
        if(userRepository.existsByUsername(request.username()) || userRepository.existsByEmail(request.email())){
            throw  new IllegalArgumentException("Username | Email already exists");
        }
        AppUser user = new AppUser();
        user.setUsername(
                request.username()
        );

        user.setEmail(
                request.email()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setRole(Role.USER);

        AppUser savedUser =
                userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public LoginResponse login(
            LoginRequest request
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        UserDetails userDetails =
                (UserDetails)
                        authentication.getPrincipal();

        String token =
                jwtService.generateToken(
                        userDetails
                );

        AppUser user =
                userRepository
                        .findByUsername(
                                request.username()
                        )
                        .orElseThrow();

        return new LoginResponse(
                token,
                "Bearer",
                user.getUsername(),
                user.getRole()
        );
    }

}
