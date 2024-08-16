package team3.cashvault.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import team3.cashvault.domain.dto.*;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.mappers.Mapper;
import team3.cashvault.services.UserService;
import team3.cashvault.services.FileUploadService;
import team3.cashvault.services.FolderNameEncoder;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import team3.cashvault.services.VerificationTokenService;
import team3.cashvault.util.ValidationUtils;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final Mapper<UserEntity, UserDto> userMapper;
    private final KeyComponent key;
    private final FileUploadService fileUploadService;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public UserController(UserService userService, Mapper<UserEntity, UserDto> userMapper, KeyComponent key,
            FileUploadService fileUploadService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.key = key;
        this.fileUploadService = fileUploadService;
        this.verificationTokenService = verificationTokenService;

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto login) {
        Optional<UserEntity> foundLogin = userService.findByEmail(login.getEmail());
        if (foundLogin.isPresent()) {
            UserEntity user = foundLogin.get();
            if (!user.getActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Your account is suspended\"}");
            } else {
                if (ControllerUtil.hashPassword(login.getHashedPassword()).equals(user.getHashedPassword())) {
                    // Return user details without sensitive information
                    String token = ControllerUtil.generateToken(user.getId(), key.getKey());
                    UserDto userDto = userMapper.mapTo(user);
                    LoginResponseDto responseDto = LoginResponseDto.builder()
                            .token(token)
                            .user(userDto)
                            .build();
                    return ResponseEntity.ok(responseDto);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Invalid credentials\"}");
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"User not found\"}");
        }
    }

    @GetMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestHeader("Authorization") String token) {

        if (ControllerUtil.isValidToken(token, key.getKey())) {
            Long id = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());
            Optional<UserEntity> tokenUser = userService.findById(id);
            UserEntity sentUser = tokenUser.get();
            if (!sentUser.getActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Your account is suspended\"}");
            } else {
                UserDto userDto = userMapper.mapTo(sentUser);
                return ResponseEntity.ok(userDto);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Invalid Token\"}");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserDetails(@RequestHeader("Authorization") String token,
            @RequestParam("userUpdate") String userUpdateJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {

        UpdateUserDto userData = null;
        try {
            userData = new ObjectMapper().readValue(userUpdateJson, UpdateUserDto.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        String validationError = ValidationUtils.validateUserChange(userData);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
        try {
            // Handle avatar upload
            if (avatar != null) {
                String folderName = FolderNameEncoder.encodeFolderName(userId);
                String fileName = "avatar.jpg";
                String avatarUrl = fileUploadService.uploadFile(folderName, fileName, avatar);
                userService.updateAvatar(userId, avatarUrl);
            }

            // Handle user details update
            if (userData != null) {
                userService.updateUserDetails(userId, userData);
            }
            Optional<UserEntity> updatedUser = userService.findById(userId);
            UserEntity sentUser = updatedUser.get();
            UserDto user = userMapper.mapTo(sentUser);
            return ResponseEntity.ok(user);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update user details: " + e.getMessage());
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordDto request) {
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());
        Optional<UserEntity> userOptional = userService.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        UserEntity user = userOptional.get();

        // Verify old password
        if (!ControllerUtil.hashPassword(request.getOldPassword()).equals(user.getHashedPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid old password.");
        }

        // Hash and update the new password
        String newHashedPassword = ControllerUtil.hashPassword(request.getNewPassword());
        userService.updateUserPassword(userId, newHashedPassword);

        return ResponseEntity.ok("Password changed successfully.");
    }

    @PutMapping("/change-email")
    public ResponseEntity<?> changeEmail(@RequestHeader("Authorization") String token,
            @RequestBody ChangeEmailDto request) {
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());
        Optional<UserEntity> userOptional = userService.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        // Verify the verification token
        if (!verificationTokenService.isVerificationTokenValid(request.getNewEmail(), request.getVerificationToken())) {
            return ResponseEntity.badRequest().body("Invalid or expired verification token.");
        }

        userService.updateUserEmail(userId, request.getNewEmail());
        Optional<UserEntity> updatedUser = userService.findById(userId);
        UserEntity sentUser = updatedUser.get();
        UserDto user = userMapper.mapTo(sentUser);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/change-card")
    public ResponseEntity<?> changeCard(@RequestHeader("Authorization") String token,
            @RequestBody ChangeCCDto request) {
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());
        Optional<UserEntity> userOptional = userService.findById(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if (!ValidationUtils.isValidCreditCardNumber(request.getCreditCardNumber())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credit card number");
        }
        userService.updateCreditCard(userId, request.getCreditCardNumber());
        Optional<UserEntity> updatedUser = userService.findById(userId);
        UserEntity sentUser = updatedUser.get();
        UserDto user = userMapper.mapTo(sentUser);
        return ResponseEntity.ok(user);
    }

}