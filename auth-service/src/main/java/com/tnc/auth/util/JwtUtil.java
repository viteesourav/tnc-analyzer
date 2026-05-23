package com.tnc.auth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

public class JwtUtil {

    // This is the server-side secert key to verify the token.
    // NOTE: have to use a very big key atleast 256-bit key for the HS256 signature
    // algorithm
    private static final String SECRET = "my-super-secret-key-for-tnc-analyzer-project-2026";

    // This generates a proper Secretkey object => Modern, more safer
    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            SECRET.getBytes(StandardCharsets.UTF_8));

    // This method generates a token -> valid for 1 hr -> the token is valid only
    // for 1 hr from time it is issued.
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }
}
