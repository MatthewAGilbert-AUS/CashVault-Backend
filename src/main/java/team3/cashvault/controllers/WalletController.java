package team3.cashvault.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team3.cashvault.domain.entities.WalletEntity;
import team3.cashvault.services.WalletService;

import java.math.BigDecimal;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/user/wallet")
public class WalletController {

    private final WalletService walletService;
    private final KeyComponent key;

    public WalletController(WalletService walletService, KeyComponent key) {
        this.walletService = walletService;
        this.key = key;
    }

    @GetMapping("/amount")
    public ResponseEntity<String> getWalletAmount(@RequestHeader("Authorization") String token) {

        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        Optional<WalletEntity> optionalWallet = walletService.findByUserId(userId);

        if (optionalWallet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wallet not found for user");
        }

        BigDecimal amount = optionalWallet.get().getBalance();
        return ResponseEntity.status(HttpStatus.OK).body(amount.toString());

    }
}
