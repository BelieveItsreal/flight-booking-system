package org.userservice.controller;

import java.util.Map;

import org.userservice.config.JwtService;
import org.userservice.dto.LoginRequestDTO;
import org.userservice.entity.User;
import org.userservice.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO requestDTO){
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(requestDTO.email(), requestDTO.password())
        );
        User user = userRepository.findByEmail((requestDTO.email()))
            .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }

}
