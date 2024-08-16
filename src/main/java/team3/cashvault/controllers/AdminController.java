package team3.cashvault.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import team3.cashvault.domain.dto.MerchantDto;
import team3.cashvault.domain.dto.UpdateUserAdminDto;
import team3.cashvault.domain.dto.UpdateUserDto;
import team3.cashvault.domain.dto.UserDto;
import team3.cashvault.domain.entities.MerchantEntity;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.domain.entities.WalletEntity;
import team3.cashvault.mappers.Mapper;
import team3.cashvault.services.MerchantService;
import team3.cashvault.services.UserService;
import team3.cashvault.services.WalletService;
import team3.cashvault.util.ValidationUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final WalletService walletService;
    private final MerchantService merchantService;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final Mapper<MerchantEntity, MerchantDto> merchantMapper;
    private final Mapper<UserEntity, UpdateUserAdminDto> userAdminMapper;
    private final KeyComponent key;

    public AdminController(UserService userService, WalletService walletService, MerchantService merchantService, Mapper<UserEntity, UserDto> userMapper, Mapper<MerchantEntity, MerchantDto> merchantMapper, Mapper<UserEntity, UpdateUserAdminDto> userAdminMapper, KeyComponent key) {
        this.userService = userService;
        this.walletService = walletService;
        this.merchantService = merchantService;
        this.userMapper = userMapper;
        this.merchantMapper = merchantMapper;
        this.userAdminMapper = userAdminMapper;
        this.key = key;
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        Optional<UserEntity> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            UserDto userDto = userMapper.mapTo(user);
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/user/{email}")
    public ResponseEntity<String> updateUser(@PathVariable String email, @RequestBody UpdateUserDto updateUserDto) {
        Optional<UserEntity> userOptional = userService.findByEmail(email);
        String validationError = ValidationUtils.validateUserChange(updateUserDto);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }
        if (userOptional.isPresent()) {
            userService.updateUserDetailsAdmin(email, updateUserDto);
            return ResponseEntity.ok("User updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/user/{email}/admin-role")
    public ResponseEntity<String> updateAdminRole(@PathVariable String email) {
        Optional<UserEntity> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            userService.updateAdminRole(email);
            return ResponseEntity.ok("Admin role updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("/user/{email}/status")
    public ResponseEntity<String> updateStatus(@PathVariable String email) {
        Optional<UserEntity> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            userService.updateUserStatus(email);
            return ResponseEntity.ok("Status changed successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!adminUser.get().getAdminRole()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User accessing this endpoint");
        }

        List<UserEntity> users = userService.getAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestHeader("Authorization") String token, @RequestBody UpdateUserAdminDto userDto) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!adminUser.get().getAdminRole()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User accessing this endpoint");
        }

        if (userService.findByEmail(userDto.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).body("User with that email is present in the database");
        }

        UserEntity user = userAdminMapper.mapFrom(userDto);
        user.setHashedPassword(ControllerUtil.hashPassword(user.getHashedPassword()));
        userService.createUser(user);
        WalletEntity walletEntity = WalletEntity.builder()
                .balance(new BigDecimal("0.00"))
                .user(user)
                .build();
        walletService.createWallet(walletEntity);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(user);

    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @RequestParam Long id) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!adminUser.get().getAdminRole()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User accessing this endpoint");
        }

        Optional<UserEntity> user = userService.findById(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested User not found");
        }

        userService.deleteById(user.get().getId());

        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token, @RequestParam Long id) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!adminUser.get().getAdminRole()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User accessing this endpoint");
        }

        Optional<UserEntity> requestUser = userService.findById(id);
        if (requestUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested User not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(requestUser);
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, @RequestBody UpdateUserAdminDto userDto) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!adminUser.get().getAdminRole()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User accessing this endpoint");
        }

        Optional<UserEntity> requestUser = userService.findByEmail(userDto.getEmail());
        if (requestUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested User not found");
        }


        // TODO fix this to map instead
        requestUser.get().setFirstName(userDto.getFirstName());
        requestUser.get().setLastName(userDto.getLastName());
        requestUser.get().setMobile(userDto.getMobile());
        requestUser.get().setUsername(userDto.getUsername());
        requestUser.get().setHashedPassword(ControllerUtil.hashPassword(userDto.getHashedPassword()));
        requestUser.get().setAvatar(userDto.getAvatar());
        requestUser.get().setProfileInfo(userDto.getProfileInfo());
        requestUser.get().setCreditCardNumber(userDto.getCreditCardNumber());
        requestUser.get().setAdminRole(userDto.getAdminRole());
        requestUser.get().setActive(userDto.getActive());
        if (!userDto.getNewEmail().isEmpty()) {
            requestUser.get().setEmail(userDto.getNewEmail());
        }

        userService.updateById(requestUser.get().getId());

        // masking password for response
        requestUser.get().setHashedPassword("*******");

        return ResponseEntity.status(HttpStatus.OK).body(requestUser.get());
    }

    @GetMapping("/merchants")
    public ResponseEntity<?> getAllMerchants(@RequestHeader("Authorization") String token) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!adminUser.get().getAdminRole()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User accessing this endpoint");
        }

        List<MerchantEntity> merchants = merchantService.getAllMerchants();

        return ResponseEntity.status(HttpStatus.OK).body(merchants);
    }

    @GetMapping("/merchant")
    public ResponseEntity<?> getMerchant(@RequestHeader("Authorization") String token, @RequestParam Long id) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!adminUser.get().getAdminRole()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User accessing this endpoint");
        }

        Optional<MerchantEntity> requestMerchant = merchantService.findById(id);
        if (requestMerchant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested Merchant not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(requestMerchant);
    }

    @PostMapping("/merchant")
    public ResponseEntity<?> createMerchant(@RequestHeader("Authorization") String token, @RequestBody MerchantDto merchantDto) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!adminUser.get().getAdminRole()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User accessing this endpoint");
        }

        if (merchantService.findByBusinessName(merchantDto.getBusinessName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).body("Merchant with that Business Name already present in the database");
        }

        MerchantEntity merchant = merchantMapper.mapFrom(merchantDto);
        merchantService.createMerchant(merchant);
        Optional<MerchantEntity> merchantOptional = merchantService.findByBusinessName(merchant.getBusinessName());

        if (merchantOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving Merchant to database.");
        }
        merchant = merchantOptional.get();

        return ResponseEntity.status(HttpStatus.OK).body(merchant);

    }

    @DeleteMapping("/merchant")
    public ResponseEntity<?> deleteMerchant(@RequestHeader("Authorization") String token, @RequestParam Long id) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!adminUser.get().getAdminRole()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User accessing this endpoint");
        }

        Optional<MerchantEntity> merchant = merchantService.findById(id);

        if (merchant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested Merchant not found");
        }

        merchantService.deleteById(merchant.get().getId());

        return ResponseEntity.status(HttpStatus.OK).body("Merchant deleted successfully.");
    }

    @PutMapping("/merchant")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, @RequestBody MerchantDto merchantDto) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (!adminUser.get().getAdminRole()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized User accessing this endpoint");
        }

        Optional<MerchantEntity> requestMerchant = merchantService.findById(merchantDto.getId());

        if (requestMerchant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested Merchant not found");
        }

        requestMerchant.get().setId(merchantDto.getId());
        requestMerchant.get().setBusinessName(merchantDto.businessName);
        requestMerchant.get().setCountry(merchantDto.getCountry());
        requestMerchant.get().setCurrency(merchantDto.getCurrency());
        requestMerchant.get().setStatus(merchantDto.status);
        requestMerchant.get().setToken(merchantDto.getToken());

        merchantService.updateById(requestMerchant.get().getId());

        return ResponseEntity.status(HttpStatus.OK).body(requestMerchant.get());
    }

}
