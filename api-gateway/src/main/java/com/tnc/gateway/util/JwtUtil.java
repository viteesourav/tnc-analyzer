package com.tnc.gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

// Role: This method validates a existing JWT Token with the secret key
public class JwtUtil {
    
    // Same SECRET Token that we have defined in auth-service JWT util -> It generates the token, here we validate it.
    private static final String SECRET = "my-super-secret-key-for-tnc-analyzer-project-2026";

    // This generates a proper Secretkey object => Modern, more safer
    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            SECRET.getBytes(StandardCharsets.UTF_8));

    // This method checks the validatity of a JWT token with the server secret key..
    public static boolean validateToken(String token) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // This method extrats the username -> added in JWTUtil.java in auth-service in generateToken method.
    public static String extractUsername(String token) {
        return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }
}
