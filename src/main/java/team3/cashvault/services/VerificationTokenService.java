package team3.cashvault.services;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenService {
    private final Map<String, String> verificationTokens = new ConcurrentHashMap<>();

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void saveVerificationToken(String email, String token) {
        verificationTokens.put(email, token);
    }

    public boolean isVerificationTokenValid(String email, String token) {
        String savedToken = verificationTokens.get(email);
        return savedToken != null && savedToken.equals(token);
    }

    public void removeVerificationToken(String email) {
        verificationTokens.remove(email);
    }
}
