package team3.cashvault.services;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FolderNameEncoder {
       // Hashes the user ID using SHA-256 algorithm and returns the encoded folder name
       public static String encodeFolderName(Long userId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(userId.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hashBytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle hashing algorithm exception
            e.printStackTrace();
            return null;
        }
}
}