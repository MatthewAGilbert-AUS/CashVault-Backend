package team3.cashvault.controllers;

import java.util.Base64;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.security.SecureRandom;
import java.security.Key;
import java.util.Date;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ControllerUtil {
    private static final int TOKEN_LENGTH = 32;

    protected static String generateToken(Long userId, Key key) {
        // Set expiration time for the token; 3600000 = 1 hour in milliseconds
        long expirationTime = 3600000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        // Build JWT token with user ID as subject and expiration date and returns it
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // userId is the ID of the logged-in user
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
    }

    // Method to validate the token
    protected static boolean isValidToken(String token, Key key) {
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "");
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(tokenWithoutBearer);
            return true;
        } catch (Exception e) {
            // Token is invalid or expired
            return false;
        }
    }

    protected static Long validateTokenAndGetUserId(String token, Key key) {
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "");
            // Parse and validate the token
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(tokenWithoutBearer);
            return Long.parseLong(claims.getBody().getSubject());
        } catch (Exception e) {
            // Token is invalid or expired
            // Token validation failed
            e.printStackTrace();
            return null;
        }
    }

    public static String generateVerificationToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        new SecureRandom().nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Apply digest to the password bytes
            byte[] hashedBytes = digest.digest(password.getBytes());
            // Convert byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle hashing algorithm not found exception
            e.printStackTrace();
            return null;
        }
    }
    
}
