package team3.cashvault.controllers;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.security.Key;

@Getter
@Component
public class KeyComponent {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
}
