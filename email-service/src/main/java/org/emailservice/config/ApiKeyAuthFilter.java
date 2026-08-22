package org.emailservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-Internal-Api-Key";
    @Value("${email.security.api-key}")
    private String expectedApiKey;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        return request.getRequestURI().startsWith("/actuator");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
    throws ServletException, IOException {
        String providedKey = request.getHeader(API_KEY_HEADER);
        if (providedKey == null || !providedKey.equals(expectedApiKey)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid or missing API key\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
