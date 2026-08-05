package org.bookingservice.config;

import org.bookingservice.enums.Role;

/**
 * Principal built directly from a verified JWT's claims - booking-service has
 * no userdb to look users up in, so this is the full identity it ever sees.
 */
public record AuthenticatedUser(Long userId, String email, Role role) {
}
