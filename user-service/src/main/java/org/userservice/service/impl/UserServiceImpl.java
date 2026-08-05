package org.userservice.service.impl;

import java.util.List;

import org.userservice.dto.UserRequestDTO;
import org.userservice.dto.UserResponseDTO;
import org.userservice.entity.User;
import org.userservice.enums.Role;
import org.userservice.exception.UserNotFoundException;
import org.userservice.mapper.UserMapper;
import org.userservice.repository.UserRepository;
import org.userservice.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.userservice.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService{


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityUtils securityUtils;



    @Override
    public UserResponseDTO createUser(UserRequestDTO userDetail) {
        User user = new User();
        user.setName(userDetail.getName());
        user.setEmail(userDetail.getEmail());
        user.setPassword(passwordEncoder.encode(userDetail.getPassword()));
        user.setRole(Role.USER);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return userMapper.toDto(userRepository.findById(id).orElseThrow(()
        -> new UserNotFoundException("User Not found with id: " +id)));
    }

    @Override
    public List<UserResponseDTO> getAllUser() {
        return userRepository.findAll().stream().map(userDetail -> userMapper.toDto(userDetail)).toList();
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()
        -> new UserNotFoundException("User not found with id: "+id));

        User currUser = securityUtils.getCurrentUser();
        if (currUser.getRole() != Role.ADMIN && !currUser.getId().equals(id)) {
            throw new AccessDeniedException("You can delete only your own account");
        }

        // Blocking deletion when the user has active bookings required a bookingdb lookup.
        // Now that booking-service owns that data, this check is deferred to a later phase
        // (a synchronous call to booking-service before deleting).
        userRepository.delete(user);
    }

    @Override
    public UserResponseDTO creatAdmin(UserRequestDTO userDetail) {
        User user = new User();
        user.setName(userDetail.getName());
        user.setEmail(userDetail.getEmail());
        user.setPassword(passwordEncoder.encode(userDetail.getPassword()));
        user.setRole(Role.ADMIN);
        return userMapper.toDto(userRepository.save(user));
    }
}
