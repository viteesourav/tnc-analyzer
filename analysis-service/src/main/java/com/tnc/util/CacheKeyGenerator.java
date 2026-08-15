package com.tnc.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

// Role: this class is specific for generating no-colliding SHA-256 keys for Redis Caching.
@Component
public class CacheKeyGenerator {

    private static final String CACHE_PREFIX = "analysis:gemini:";

    public String generateKey(String text) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    text.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hexString = new StringBuilder();

            for (byte b: hash) {
                hexString.append(
                    String.format("%02x", b)
                );
            }

            return CACHE_PREFIX + hexString;


        } catch (NoSuchAlgorithmException e) {
            
            throw new IllegalStateException(
                "SHA-256 algorithm is not available",
                e
            );
        }

    }
    
}

/*
    NOTE:
        -- Here the process of hashing the input text string is: 
            1. First convert the input incoming string text to byte charsets. -> using the MessageDigest's SHA-256 algo.
            2. then use a string builder.
            3. Iterate the byte charsets, and build the hashed Key.

*/
