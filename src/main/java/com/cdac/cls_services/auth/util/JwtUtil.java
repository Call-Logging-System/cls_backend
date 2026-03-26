package com.cdac.cls_services.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public void generateTokenAndSetCookie(Long userId, String email, Integer role,
                                          HttpServletResponse response) {
        String token = Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();

        Cookie cookie = new Cookie("cls_token", token);
        cookie.setHttpOnly(true);       // JS cannot read this
        cookie.setSecure(false);        // Set true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge((int) (expirationMs / 1000));
        response.addCookie(cookie);
    }

    public void clearCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("cls_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);        // Match the setting above
        cookie.setPath("/");
        cookie.setMaxAge(0);            // Immediately expire
        response.addCookie(cookie);
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("cls_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // Claims is just a Map of all the data stored in the token payload.

//    1) Decodes the token
//    2) Re-computes the signature using your secret key
//    3) Compares it to the signature in the token
//    4) If they match → token is genuine, returns the payload (Claims)
//    5) If they don't match or token is expired → throws a JwtException
    public Claims validateAndExtractClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token){
        return validateAndExtractClaims(token).getSubject();
    }

    public Long extractUserId(String token){
        return validateAndExtractClaims(token).get("userId",Long.class);
    }

    public Integer extractRole(String token){
        return validateAndExtractClaims(token).get("role",Integer.class);
    }

    public boolean isTokenValid(String token) {
        try {
            validateAndExtractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


}
