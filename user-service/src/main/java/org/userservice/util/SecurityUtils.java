package org.userservice.util;

import org.userservice.entity.User;
import org.userservice.exception.UserNotFoundException;
import org.userservice.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    private final UserRepository userRepository;

    public SecurityUtils(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User getCurrentUser(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(()-> new UserNotFoundException("user not found"));
    }
}
