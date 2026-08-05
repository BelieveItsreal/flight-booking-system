package org.userservice.controller;

import jakarta.validation.Valid;

import java.util.List;

import org.userservice.dto.UserRequestDTO;
import org.userservice.dto.UserResponseDTO;
import org.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userDetail) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDetail));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponseDTO>getById(@PathVariable long id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>>getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUser());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
