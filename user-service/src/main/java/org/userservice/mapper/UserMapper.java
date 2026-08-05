package org.userservice.mapper;

import org.userservice.dto.UserResponseDTO;
import org.userservice.entity.User;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {
    public UserResponseDTO toDto(User user){
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
