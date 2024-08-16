package team3.cashvault.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team3.cashvault.domain.TransactionType;
import team3.cashvault.domain.dto.BPayDto;
import team3.cashvault.domain.dto.CreateTransactionDto;
import team3.cashvault.domain.dto.TransactionDisplayDto;
import team3.cashvault.domain.dto.TransactionDto;
import team3.cashvault.domain.dto.TransactionReturnDto;
import team3.cashvault.domain.entities.MerchantEntity;
import team3.cashvault.domain.entities.TransactionEntity;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.domain.entities.WalletEntity;
import team3.cashvault.mappers.Mapper;
import team3.cashvault.services.BPayService;
import team3.cashvault.services.CurrencyConversionService;
import team3.cashvault.services.MerchantService;
import team3.cashvault.services.TransactionService;
import team3.cashvault.services.UserService;
import team3.cashvault.services.WalletService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/user/transaction")
public class TransactionController {

    private final UserService userService;
    private final MerchantService merchantService;
    private final TransactionService transactionService;
    private final Mapper<TransactionEntity, TransactionDto> transactionMapper;
    private final WalletService walletService;
    private final KeyComponent key;
    private final CurrencyConversionService convert;
    private final BPayService bPay;

    public TransactionController(UserService userService, MerchantService merchantService,
            TransactionService transactionService,
            Mapper<TransactionEntity, TransactionDto> transactionMapper,
            WalletService walletService, CurrencyConversionService convert, KeyComponent key, BPayService bPay) {
        this.userService = userService;
        this.merchantService = merchantService;
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
        this.walletService = walletService;
        this.convert = convert;
        this.key = key;
        this.bPay = bPay;

    }
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentTransactions(
            @RequestHeader("Authorization") String token){
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Optional<Iterable<TransactionEntity>> transactions = transactionService.findAllByUserId(userId);
        if (transactions.isPresent()) {
            ArrayList<TransactionDisplayDto> transactionsDto = new ArrayList<>();
            for (TransactionEntity transaction : transactions.get()) {

                TransactionDto dto = transactionMapper.mapTo(transaction);
                TransactionReturnDto newDto = TransactionReturnDto.builder()
                        .id(dto.getId())
                        .currency(dto.getCurrency())
                        .transactionTime(dto.getTransactionTime())
                        .transactionType(dto.getTransactionType())
                        .amount(dto.getAmount())
                        .destinationId(dto.getDestinationId())
                        .merchantId(dto.getMerchantId())
                        .biller(dto.getBiller())
                        .build();

                if (transaction.getBiller() != null) {
                    newDto.setDestination(transaction.getBiller());
                } else if (transaction.getMerchantId() != null) {
                    // Retrieve destination name based on merchant ID
                    Optional<MerchantEntity> merchantEntityOptional = merchantService
                            .findById(transaction.getMerchantId());
                    if (merchantEntityOptional.isPresent()) {
                        newDto.setDestination(merchantEntityOptional.get().getBusinessName());
                    }
                } else if (transaction.getDestinationId() != null) {
                    Optional<UserEntity> destinationUser = userService.findById(newDto.getDestinationId());
                    if (destinationUser.isPresent()) {
                        newDto.setDestination(
                                destinationUser.get().getFirstName() + " " + destinationUser.get().getLastName());
                    }
                }

                String name = optionalUser.get().getFirstName() + " " + optionalUser.get().getLastName();
                // format date time to show as string as it is received from the frontend
                String formattedTransactionTime = newDto.getTransactionTime()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                BigDecimal fees = BigDecimal.ZERO;
                if (!dto.getCurrency().contains("AUD") && dto.getTransactionType().toString().equals("WITHDRAW")) {
                    // divide by 1.01 to have the conversion without the fee attached
                    BigDecimal feeDivision = new BigDecimal("1.01");
                    BigDecimal fee = dto.getAmount().divide(feeDivision, 8, RoundingMode.HALF_UP);
                    fee = dto.getAmount().subtract(fee);
                    fees = fees.add(fee);
                    fees = fee.setScale(2, RoundingMode.HALF_UP);

                }

                // display dto wasn't really needed but as stated want to hide ids
                TransactionDisplayDto display = TransactionDisplayDto.builder()
                        .name(name)
                        .currency(newDto.getCurrency())
                        .transactionTime(formattedTransactionTime)
                        .transactionType(newDto.getTransactionType())
                        .amount(newDto.getAmount())
                        .destination(newDto.getDestination())
                        .fees(fees)
                        .build();

                transactionsDto.add(display);
            }

            Collections.reverse(transactionsDto);
            ArrayList<TransactionDisplayDto> recentTransactions = new ArrayList<>();

            if (transactionsDto.size() >= 5){
                for(int i = 0; i < 5; i++){
                    recentTransactions.add(transactionsDto.get(i));

                }
            }
            else{
                recentTransactions = transactionsDto;
            }



            return ResponseEntity.status(HttpStatus.OK).body(recentTransactions);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No transactions found");
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllTransactions(
            @RequestHeader("Authorization") String token) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Optional<Iterable<TransactionEntity>> transactions = transactionService.findAllByUserId(userId);
        if (transactions.isPresent()) {
            List<TransactionDisplayDto> transactionsDto = new ArrayList<>();
            for (TransactionEntity transaction : transactions.get()) {

                TransactionDto dto = transactionMapper.mapTo(transaction);
                TransactionReturnDto newDto = TransactionReturnDto.builder()
                        .id(dto.getId())
                        .currency(dto.getCurrency())
                        .transactionTime(dto.getTransactionTime())
                        .transactionType(dto.getTransactionType())
                        .amount(dto.getAmount())
                        .destinationId(dto.getDestinationId())
                        .merchantId(dto.getMerchantId())
                        .biller(dto.getBiller())
                        .build();

                if (transaction.getBiller() != null) {
                    newDto.setDestination(transaction.getBiller());
                } else if (transaction.getMerchantId() != null) {
                    // Retrieve destination name based on merchant ID
                    Optional<MerchantEntity> merchantEntityOptional = merchantService
                            .findById(transaction.getMerchantId());
                    if (merchantEntityOptional.isPresent()) {
                        newDto.setDestination(merchantEntityOptional.get().getBusinessName());
                    }
                } else if (transaction.getDestinationId() != null) {
                    Optional<UserEntity> destinationUser = userService.findById(newDto.getDestinationId());
                    if (destinationUser.isPresent()) {
                        newDto.setDestination(
                                destinationUser.get().getFirstName() + " " + destinationUser.get().getLastName());
                    }
                }

                String name = optionalUser.get().getFirstName() + " " + optionalUser.get().getLastName();
                // format date time to show as string as it is received from the frontend
                String formattedTransactionTime = newDto.getTransactionTime()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                BigDecimal fees = BigDecimal.ZERO;
                if (!dto.getCurrency().contains("AUD") && dto.getTransactionType().toString().equals("WITHDRAW")) {
                    // divide by 1.01 to have the conversion without the fee attached
                    BigDecimal feeDivision = new BigDecimal("1.01");
                    BigDecimal fee = dto.getAmount().divide(feeDivision, 8, RoundingMode.HALF_UP);
                    fee = dto.getAmount().subtract(fee);
                    fees = fees.add(fee);
                    fees = fee.setScale(2, RoundingMode.HALF_UP);

                }

                // display dto wasn't really needed but as stated want to hide ids
                TransactionDisplayDto display = TransactionDisplayDto.builder()
                        .name(name)
                        .currency(newDto.getCurrency())
                        .transactionTime(formattedTransactionTime)
                        .transactionType(newDto.getTransactionType())
                        .amount(newDto.getAmount())
                        .destination(newDto.getDestination())
                        .fees(fees)
                        .build();

                transactionsDto.add(display);
            }
            Collections.reverse(transactionsDto);
            return ResponseEntity.status(HttpStatus.OK).body(transactionsDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No transactions found");
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> createTransaction(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateTransactionDto createTransactionDto) {

        BigDecimal value = createTransactionDto.getAmount();
        // validate the amount and destination have values
        if (createTransactionDto.getAmount() == null || createTransactionDto.getDestination() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Null values provided");
        }

        if (createTransactionDto.getAmount().compareTo(new BigDecimal("0")) < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount provided was not greater than zero");
        }

        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        // get both user and destination destination from email as id not known
        Optional<UserEntity> sender = userService.findById(userId);
        Optional<UserEntity> recipient = userService.findByEmail(createTransactionDto.getDestination());
        // check if user and destination exist
        if (sender.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (recipient.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        // check to see if trying to send money to thyself
        if (sender.equals(recipient)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Destination specified was self");
        }

        // wallet for the sender
        Optional<WalletEntity> optionalSenderWallet = walletService.findByUserId(userId);

        if (optionalSenderWallet.isEmpty()) {
            // This shouldn't happen because when a user is created a wallet is created
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("WALLET NOT FOUND FOR USER: CONTACT ADMINISTRATOR");
        }
        BigDecimal moneys = optionalSenderWallet.get().getBalance();
        BigDecimal request = createTransactionDto.getAmount();
        // Compare moneys with request
        int comparisonResult = moneys.compareTo(request);

        // have enough money
        if (comparisonResult < 0) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    "Not enough funds in wallet to create transaction");
        }

        // get the destination wallet
        Optional<WalletEntity> optionalRecipientWallet = walletService.findByUserId(recipient.get().getId());
        if (optionalRecipientWallet.isEmpty()) {
            // This shouldn't happen because when a user is created a wallet is created
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wallet belonging to destination id not found");
        }
        // Defaults currency to AUD if not specified and converts if sepecified
        if (createTransactionDto.getCurrency() == null) {
            createTransactionDto.setCurrency("AUD");
        } else if (!createTransactionDto.getCurrency().equals("AUD")) {
            try {
                BigDecimal conversion = convert.convertCurrency("AUD", createTransactionDto.getCurrency(),
                        createTransactionDto.getAmount());
                createTransactionDto.setAmount(conversion);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        "Failed to convert currency");
            }
        }
        // update wallet amounts
        BigDecimal divider = new BigDecimal("1.01");
        BigDecimal transactionAmount = createTransactionDto.getAmount();

        BigDecimal receivedAmount = BigDecimal.ZERO;
        if (createTransactionDto.getCurrency().equals("AUD")) {
            receivedAmount = transactionAmount;
        } else {
            receivedAmount = createTransactionDto.getAmount().divide(divider, 8, RoundingMode.HALF_UP);
        }
        WalletEntity senderWallet = optionalSenderWallet.get();
        WalletEntity recipientWallet = optionalRecipientWallet.get();
        // Update sender's wallet balance
        BigDecimal senderBalance = senderWallet.getBalance().subtract(transactionAmount);
        senderWallet.setBalance(senderBalance);
        walletService.updateWallet(senderWallet);

        // Update recipient's wallet balance
        BigDecimal recipientBalance = recipientWallet.getBalance().add(receivedAmount);
        recipientWallet.setBalance(recipientBalance);
        walletService.updateWallet(recipientWallet);

        // creates a xfer entity to input into transactions
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .currency(createTransactionDto.getCurrency() + " - " + value)
                .transactionTime(LocalDateTime.now(ZoneOffset.UTC))
                .amount(transactionAmount.negate())
                .transactionType(TransactionType.WITHDRAW)
                .destinationId(recipient.get().getId())
                .user(sender.get())
                .build();

        // destination entity filled with matching values of sender enitity
        TransactionEntity destinationEntity = TransactionEntity.builder()
                .transactionType(TransactionType.DEPOSIT)
                .currency(transactionEntity.getCurrency())
                .transactionTime(transactionEntity.getTransactionTime())
                .amount(receivedAmount)
                .user(recipient.get())
                .destinationId(userId)
                .build();

        // update both the sent and recieved to withdraw and deposit
        transactionService.updateTransaction(destinationEntity);
        transactionService.updateTransaction(transactionEntity);

        return ResponseEntity.status(HttpStatus.OK).body("Transaction created.");
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMoney(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateTransactionDto createTransactionDto) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Optional<WalletEntity> optionalWallet = walletService.findByUserId(userId);

        if (optionalWallet.isEmpty()) {
            // This shouldn't happen because when a user is created a wallet is created
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("WALLET NOT FOUND FOR USER: CONTACT ADMINISTRATOR");
        }

        WalletEntity wallet = optionalWallet.get();

        BigDecimal amountToAdd = createTransactionDto.getAmount();

        // Update wallet balance
        BigDecimal newBalance = wallet.getBalance().add(amountToAdd);
        wallet.setBalance(newBalance);
        walletService.updateWallet(wallet);

        TransactionDto transactionDto = TransactionDto.builder()
                .currency(createTransactionDto.getCurrency())
                .transactionTime(LocalDateTime.now(ZoneOffset.UTC))
                .amount(createTransactionDto.getAmount())
                .transactionType(TransactionType.DEPOSIT)
                .destinationId(userId)
                .build();

        // Defaults currency to AUD if not specified
        if (transactionDto.getCurrency() == null) {
            transactionDto.setCurrency("AUD");
        }

        TransactionEntity transactionEntity = transactionMapper.mapFrom(transactionDto);
        transactionEntity.setUser(optionalUser.get());
        transactionService.updateTransaction(transactionEntity);

        return ResponseEntity.status(HttpStatus.OK).body("Transaction created.");
    }

    @PostMapping("/pay")
    public ResponseEntity<?> makePayment(
            @RequestHeader("Authorization") String token,
            @RequestBody BPayDto payment) {

        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> optionalUser = userService.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Retrieve user's wallet
        Optional<WalletEntity> optionalWallet = walletService.findByUserId(userId);
        if (optionalWallet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wallet not found for user");
        }

        WalletEntity wallet = optionalWallet.get();
        BigDecimal currentBalance = wallet.getBalance();

        // Check if the user has enough balance for the payment
        if (currentBalance.compareTo(payment.getAmount()) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance for the payment");
        }

        // Deduct amount from user's wallet
        BigDecimal newBalance = currentBalance.subtract(payment.getAmount());
        wallet.setBalance(newBalance);
        walletService.updateWallet(wallet);

        String billerDetails = bPay.lookupBillerCode(payment.getBillerCode());
        if (billerDetails == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Biller does not exist");
        }
        String shortName = null;
        if (billerDetails != null) {
            try {
                // Parse JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(billerDetails);

                // Extract shortName
                shortName = jsonNode.get("shortName").asText();
            } catch (IOException e) {
                e.printStackTrace();
                shortName = "Unknown"; // Default value in case of error
            }
        } 

        // Create a transaction record for the payment
        TransactionEntity paymentTransaction = TransactionEntity.builder()
                .currency("AUD") // Assuming payments are made in AUD
                .transactionTime(LocalDateTime.now(ZoneOffset.UTC))
                .amount(payment.getAmount().negate())
                .transactionType(TransactionType.WITHDRAW)
                .user(optionalUser.get())
                .biller(payment.getBillerCode() + " - " + shortName)
                .build();

        transactionService.updateTransaction(paymentTransaction);

        return ResponseEntity.status(HttpStatus.OK).body("Payment completed successfully.");
    }

    // merchant payment
    public String payMerchant(String email, BigDecimal amount, String token) {

        BigDecimal conversions = BigDecimal.ZERO;

        Optional<MerchantEntity> merchantOptional = merchantService.findByToken(token);
        if (merchantOptional.isEmpty()) {
            return "Error: Merchant not found";
        }
        MerchantEntity merchant = merchantOptional.get();
        if (!merchant.getCurrency().equals("AUD")) {
            try {
                conversions = convert.convertCurrency("AUD", merchant.getCurrency(),
                        amount);
            } catch (IOException e) {

                return "Error in converting";
            }
        } else {
            conversions = amount;
        }

        Optional<UserEntity> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            return "Error: User not found";
        }
        UserEntity user = userOptional.get();

        // Retrieve user's wallet
        Optional<WalletEntity> optionalWallet = walletService.findByUserId(user.getId());

        if (optionalWallet.isEmpty()) {
            return "Wallet not found for user";
        }
        WalletEntity wallet = optionalWallet.get();
        // Transfer money from user's wallet to merchant's account
        BigDecimal userBalance = wallet.getBalance();
        if (userBalance.compareTo(conversions) < 0) {
            return "Error: Insufficient funds";
        }
        BigDecimal newBalance = userBalance.subtract(conversions);
        wallet.setBalance(newBalance);

        TransactionEntity paymentTransaction = TransactionEntity.builder()
                .currency(merchant.getCurrency() + " - " + amount)
                .transactionTime(LocalDateTime.now(ZoneOffset.UTC))
                .amount(conversions.negate())
                .transactionType(TransactionType.WITHDRAW)
                .user(user)
                .merchantId(merchant.getId())
                .build();

        transactionService.updateTransaction(paymentTransaction);

        return "Payment successful";

    }
}
