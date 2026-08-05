package org.userservice.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService){
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException{
        //read the Authorization header
        String authHeader = request.getHeader("Authorization");

        //if no token, skip - let spring security handle it as authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //extract the token but remove "Bearer " prefix
        String token = authHeader.substring(7);

        //extract email from token
        String email = jwtService.extractEmail(token);

        //if email is found but no  authentication set yet in context
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //load user from db
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            //validate token
            if (jwtService.isTokenValid(token, email)) {
                //set authetication in sercurity context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        //call filter chain
        filterChain.doFilter(request, response);

    }

}
