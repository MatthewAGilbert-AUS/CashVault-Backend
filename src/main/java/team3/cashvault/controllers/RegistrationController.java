package team3.cashvault.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team3.cashvault.domain.dto.*;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.domain.entities.WalletEntity;
import team3.cashvault.services.UserService;
import team3.cashvault.services.WalletService;
import team3.cashvault.services.EmailService;
import team3.cashvault.services.VerificationTokenService;
import java.math.BigDecimal;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import team3.cashvault.util.ValidationUtils;

@RestController
@RequestMapping("/user")
public class RegistrationController {

    private final UserService userService;
    private final WalletService walletService;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public RegistrationController(UserService userService, WalletService walletService,
            EmailService emailService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.walletService = walletService;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;

    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody LoginDto data) {
        String email = data.getEmail();

        try {
            Optional<UserEntity> existingUser = userService.findByEmail(email);
            if (existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already registered.");
            }
            // Generate a verification token
            String verificationToken = verificationTokenService.generateToken();

            // Save the verification token with the email as the key
            verificationTokenService.saveVerificationToken(email, verificationToken);

            // Send verification email
            emailService.sendVerificationEmail(email, verificationToken);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"message\": \"Verification email sent successfully.\", \"email\": \"" + email
                            + "\", \"token\": \"" + verificationToken + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send verification email.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerAccount(@RequestBody NewUserDto newUser) {
        try {
            String validationError = ValidationUtils.validateUser(newUser);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(validationError);
            }

            // Check if the verification token matches the one sent to the email
            if (!verificationTokenService.isVerificationTokenValid(newUser.getEmail(),
                    newUser.getVerificationToken())) {
                return ResponseEntity.badRequest().body("Invalid or expired verification token.");
            }
            String hashedPassword = ControllerUtil.hashPassword(newUser.getHashedPassword());
            UserEntity userEntity = UserEntity.builder()
                    .email(newUser.getEmail())
                    .firstName(newUser.getFirstName())
                    .lastName(newUser.getLastName())
                    .mobile(newUser.getMobile())
                    .username(newUser.getUsername())
                    .hashedPassword(hashedPassword)
                    .adminRole(false)
                    .profileInfo("Edit me if you wish....")
                    .avatar("https://storage.googleapis.com/cashvault-bucket/default.png")
                    .creditCardNumber(newUser.getCreditCardNumber())
                    .active(true)
                    .build();
            // Register the user (without password hashing for now)
            WalletEntity walletEntity = WalletEntity.builder()
                    .balance(new BigDecimal("0.00"))
                    .user(userEntity)
                    .build();
            // Save the new user entity to the database
            userService.createUser(userEntity);
            walletService.createWallet(walletEntity);

            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("User registration failed: Internal server error.");
        }
    }

}