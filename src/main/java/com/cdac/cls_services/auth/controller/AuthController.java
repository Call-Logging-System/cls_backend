package com.cdac.cls_services.auth.controller;

import com.cdac.cls_services.auth.JwtUtil;
import com.cdac.cls_services.auth.dto.AuthResponseDto;
import com.cdac.cls_services.auth.dto.LoginRequestDto;
import com.cdac.cls_services.call_logs.models.UserModel;
import com.cdac.cls_services.call_logs.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody LoginRequestDto dto, HttpServletResponse response){
        try {
            // Step 1: Ask Spring Security to verify email + password
            // Internally this calls UserDetailsServiceImpl.loadUserByUsername()
            // then BCrypt-compares the passwords
            // If wrong credentials → throws BadCredentialsException

            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

            // Step 2: Credentials are valid — load the full user from DB
            // We need userId, name, role which aren't in UserDetails

            UserModel user = userRepo.findByEmail(dto.getEmail()).orElseThrow();

            // Step 3: Generate JWT and set it as HttpOnly cookie on the response

            jwtUtil.generateTokenAndSetCookie(user.getId(), user.getEmail(), user.getRole(), response);

            // Step 4: Return user info as JSON
            // Angular stores this in memory to know who is logged in

            return ResponseEntity.ok(new AuthResponseDto(user.getId(), user.getName(), user.getEmail(), user.getRole(), "Login Successful"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during login");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Simply clear the JWT cookie — no server state to clean up (stateless)
        jwtUtil.clearCookie(response);
        return ResponseEntity.ok("Logged out successfully");
    }

    // Angular can't read the HttpOnly cookie directly
    // This endpoint reads the JWT server-side and returns the user info as JSON
    // Angular calls this on app startup to restore login state

    @GetMapping("/me")
    public ResponseEntity<?>  getCurrentUser(HttpServletRequest request){
        // Extract token from cookie
        String token = jwtUtil.extractTokenFromRequest(request);

        if(token == null || !jwtUtil.isTokenValid(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Not authenticated");
        }

        // Extract claims from token
        String email = jwtUtil.extractEmail(token);
        Integer role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);

        // Load name from DB (name isn't stored in JWT)
        UserModel user = userRepo.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(new AuthResponseDto(
                userId,
                user.getName(),
                email,
                role,
                "Authenticated"
        ));

    }
}
