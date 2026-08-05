package org.userservice.controller;

import org.userservice.dto.UserRequestDTO;
import org.userservice.dto.UserResponseDTO;
import org.userservice.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/create-admin")
    public ResponseEntity<UserResponseDTO> createAdmin(@Valid @RequestBody UserRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.creatAdmin(request));
    }

}
