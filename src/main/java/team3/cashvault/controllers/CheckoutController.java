package team3.cashvault.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team3.cashvault.domain.dto.MerchantPayDto;
import team3.cashvault.domain.entities.MerchantEntity;
import team3.cashvault.services.AuthService;
import team3.cashvault.services.MerchantService;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final AuthService authService;
    private final TransactionController transactionController;
    private final MerchantService merchantService;

    public CheckoutController(AuthService authService, TransactionController transactionController,
            MerchantService merchantService) {
        this.authService = authService;
        this.transactionController = transactionController;
        this.merchantService = merchantService;
    }

    @GetMapping("/initiate")
    public ResponseEntity<String> initiatePayment(
            @RequestParam String token, @RequestParam BigDecimal amount) {
        // Verify the user's authentication token
        Optional<MerchantEntity> tokenMerchant = merchantService.findByToken(token);
        if (tokenMerchant.isPresent()) {
            try {
                    // Load the HTML content from the classpath resource
            ClassPathResource resource = new ClassPathResource("static/paymentForm.html");
            InputStream inputStream = resource.getInputStream();
            StringBuilder htmlContentBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    htmlContentBuilder.append(line);
                }
            }

            String htmlContent = htmlContentBuilder.toString();

            // Replace placeholders with actual values
            htmlContent = htmlContent.replace("{{token}}", token)
                    .replace("{{amount}}", amount.toString());

            return ResponseEntity.ok(htmlContent);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading HTML file");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Merchant not found");
        }
    }

    @PostMapping("/loginAndPay")
    public ResponseEntity<String> loginUser(
          @RequestBody MerchantPayDto payMerchant) {
        // Authenticate user and return authToken
        if (!authService.authenticateUser(payMerchant.getEmail(), payMerchant.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String paymentResult = transactionController.payMerchant(payMerchant.getEmail(), payMerchant.getAmount(), payMerchant.getToken());
        if (paymentResult.equals("Payment successful")) {
            return ResponseEntity.ok("Payment Successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(paymentResult);
        }

    }
}
