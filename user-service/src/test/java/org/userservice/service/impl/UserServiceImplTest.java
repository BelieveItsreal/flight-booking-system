package org.userservice.service.impl;

import org.userservice.dto.UserRequestDTO;
import org.userservice.dto.UserResponseDTO;
import org.userservice.entity.User;
import org.userservice.enums.Role;
import org.userservice.exception.UserNotFoundException;
import org.userservice.mapper.UserMapper;
import org.userservice.repository.UserRepository;
import org.userservice.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SecurityUtils securityUtils;

    @InjectMocks
    private UserServiceImpl userService;

    // ---- createUser ----

    @Test
    void createUser_encodesPasswordSetsRoleUserAndReturnsDTO() {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", "rawpass");
        User savedUser = new User();
        UserResponseDTO dto = new UserResponseDTO(1L, "Alice", "alice@example.com");

        when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(dto);

        UserResponseDTO result = userService.createUser(request);

        assertThat(result).isSameAs(dto);
        verify(userRepository).save(argThat(u ->
                "alice@example.com".equals(u.getEmail()) &&
                "encodedpass".equals(u.getPassword()) &&
                u.getRole() == Role.USER
        ));
    }

    // ---- getUserById ----

    @Test
    void getUserById_found_returnsDTO() {
        User user = new User();
        UserResponseDTO dto = new UserResponseDTO();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        assertThat(userService.getUserById(1L)).isSameAs(dto);
    }

    @Test
    void getUserById_notFound_throwsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ---- getAllUser ----

    @Test
    void getAllUser_returnsMappedList() {
        User user = new User();
        UserResponseDTO dto = new UserResponseDTO();

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        assertThat(userService.getAllUser()).containsExactly(dto);
    }

    // ---- createAdmin ----

    @Test
    void createAdmin_encodesPasswordSetsRoleAdminAndReturnsDTO() {
        UserRequestDTO request = new UserRequestDTO("Admin", "admin@example.com", "adminpass");
        User savedUser = new User();
        UserResponseDTO dto = new UserResponseDTO(2L, "Admin", "admin@example.com");

        when(passwordEncoder.encode("adminpass")).thenReturn("encodedadmin");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(dto);

        UserResponseDTO result = userService.creatAdmin(request);

        assertThat(result).isSameAs(dto);
        verify(userRepository).save(argThat(u ->
                u.getRole() == Role.ADMIN &&
                "encodedadmin".equals(u.getPassword())
        ));
    }

    // ---- deleteUser ----

    @Test
    void deleteUser_userNotFound_throwsUserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteUser_nonAdminDeletingAnotherUser_throwsAccessDeniedException() {
        User target = buildUser(1L, Role.USER);
        User currentUser = buildUser(2L, Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(target));
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteUser_userDeletesOwnAccount_succeeds() {
        User user = buildUser(1L, Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(securityUtils.getCurrentUser()).thenReturn(user);

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_adminDeletesAnotherUser_succeeds() {
        User target = buildUser(1L, Role.USER);
        User admin = buildUser(99L, Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(target));
        when(securityUtils.getCurrentUser()).thenReturn(admin);

        userService.deleteUser(1L);

        verify(userRepository).delete(target);
    }

    // ---- helpers ----

    private User buildUser(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return user;
    }
}
