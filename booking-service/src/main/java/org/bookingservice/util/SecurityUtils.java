package org.bookingservice.util;

import org.bookingservice.config.AuthenticatedUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public AuthenticatedUser getCurrentUser(){
        return (AuthenticatedUser) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
    }
}
