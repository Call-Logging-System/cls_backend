package com.cdac.cls_services.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get the JWT from the cls_token cookie
        String token = jwtUtil.extractTokenFromRequest(request);

        // if no token found, skip the filter entirely
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if token is valid (not expired, not tampered with)
        if(!jwtUtil.isTokenValid(token)){
            filterChain.doFilter(request,response);
            return;
        }

        // Extract the email from the token's claims
        String email = jwtUtil.extractEmail(token);

        // Setting authentication if not already set for this request
        // SecurityContextHolder holds the current user's auth for this request
        if(email!=null && SecurityContextHolder.getContext().getAuthentication() == null){

            // Load full user details from the database using the email
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Create an authentication object — this is the "name badge"
            // It holds: who the user is, their credentials, and their roles/permissions
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // Attach request details (IP, session info) to the auth token
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set the authentication in the SecurityContext
            // After this line, Spring knows this request belongs to a valid user
            SecurityContextHolder.getContext().setAuthentication(authToken);

        }
        // Pass the request along to the next filter / controller
        filterChain.doFilter(request, response);

    }
}
