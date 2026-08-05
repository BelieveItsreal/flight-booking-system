package org.userservice.service;

import java.util.List;

import org.userservice.dto.UserRequestDTO;
import org.userservice.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userDetail);
    UserResponseDTO getUserById(Long id);
    List<UserResponseDTO> getAllUser();
    void deleteUser(Long id);
    UserResponseDTO creatAdmin(UserRequestDTO userDetail);
}
