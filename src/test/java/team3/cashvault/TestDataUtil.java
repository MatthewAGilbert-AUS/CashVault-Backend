package team3.cashvault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import team3.cashvault.domain.TransactionType;
import team3.cashvault.domain.dto.LoginDto;
import team3.cashvault.domain.dto.NewUserDto;
import team3.cashvault.domain.entities.FriendEntity;
import team3.cashvault.domain.entities.TransactionEntity;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.domain.entities.WalletEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TestDataUtil {
    private TestDataUtil() {
    }

    public static UserEntity createTestUserEntityA() {
        return UserEntity.builder()
                .firstName("Matthew")
                .lastName("Gilbert")
                .email("matthew@email.com")
                .adminRole(false)
                .hashedPassword("123")
                .username("mgilbert")
                .mobile("0422222222")
                .active(true)
                .creditCardNumber("0123456789102345")
                .build();
    }

    public static UserEntity createTestUserEntityB() {
        return UserEntity.builder()
                .firstName("Karsten")
                .lastName("Beck")
                .email("karsten@email.com")
                .adminRole(false)
                .hashedPassword("456")
                .username("karstenB")
                .mobile("0455555555")
                .active(true)
                .creditCardNumber("1234567891011213")
                .build();
    }

    public static UserEntity createTestUserEntityC() {
        return UserEntity.builder()
                .firstName("Channon")
                .lastName("Harper")
                .email("Channon@email.com")
                .adminRole(false)
                .hashedPassword("abc")
                .username("Channon1337")
                .mobile("0413371337")
                .active(true)
                .creditCardNumber("4758567890123456")
                .build();
    }

    public static UserEntity createTestUserEntityD() {
        return UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .email("JohnDoe@email.com")
                .adminRole(false)
                .hashedPassword("abc123")
                .username("JDoe97")
                .mobile("0412244437")
                .active(true)
                .creditCardNumber("4000567812349010")
                .build();
    }

    public static WalletEntity createTestWalletEntity(UserEntity user) {
        return WalletEntity.builder()
                .balance(new BigDecimal("550.95"))
                .user(user)
                .build();
    }

    public static WalletEntity createTestWalletEntity(UserEntity user, BigDecimal amount) {
        return WalletEntity.builder()
                .balance(amount)
                .user(user)
                .build();
    }

    public static TransactionEntity createTransactionEntityA(UserEntity user) {
        return TransactionEntity.builder()
                .amount(new BigDecimal("50.50"))
                .transactionType(TransactionType.DEPOSIT)
                .currency("AUD")
                .transactionTime(LocalDateTime.of(2024, 1, 10, 9, 3))
                .user(user)
                .build();
    }

    public static TransactionEntity createTransactionEntityB(UserEntity user) {
        return TransactionEntity.builder()
                .amount(new BigDecimal("98.30"))
                .transactionType(TransactionType.WITHDRAW)
                .currency("AUD")
                .transactionTime(LocalDateTime.of(2024, 2, 13, 11, 44))
                .user(user)
                .build();
    }

    public static TransactionEntity createTransactionEntityC(UserEntity user) {
        return TransactionEntity.builder()
                .amount(new BigDecimal("101.10"))
                .transactionType(TransactionType.DEPOSIT)
                .currency("AUD")
                .transactionTime(LocalDateTime.of(2024, 3, 30, 6, 34))
                .user(user)
                .build();
    }

    public static FriendEntity createFriendship(UserEntity userEntity, UserEntity friend) {
        return FriendEntity.builder()
                .user(userEntity)
                .friend(friend)
                .build();
    }


    public static String getLoginToken(MockMvc mockMvc, ObjectMapper objectMapper, UserEntity user) throws Exception {
        LoginDto loginDto = LoginDto.builder()
                .email(user.getEmail())
                .hashedPassword(user.getHashedPassword())
                .build();

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto))).andReturn();
        String responseJson = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseJson);

        return jsonNode.get("token").asText();
    }

    private static String serializeEmail(String email) {
        return "{\"email\":\"" + email + "\"}";
    }

    public static String getVerifiedEmailToken(MockMvc mockMvc, ObjectMapper objectMapper, String email) throws Exception {
        String emailJson = serializeEmail(email);

        MvcResult emailCode = mockMvc.perform(
                MockMvcRequestBuilders.post("/user/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailJson)
        ).andReturn();

        String verifyEmailResponse = emailCode.getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(verifyEmailResponse);

        return jsonNode.get("token").asText();
    }

    public static String createRegisterUserRequestBody(MockMvc mockMvc, ObjectMapper objectMapper, UserEntity user) throws Exception {
        NewUserDto newUserDto = NewUserDto.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .mobile(user.getMobile())
                .username(user.getUsername())
                .hashedPassword(user.getHashedPassword())
                .verificationToken(getVerifiedEmailToken(mockMvc, objectMapper, user.getEmail()))
                .creditCardNumber(user.getCreditCardNumber())
                .dob("1990-01-01")
                .build();

        return objectMapper.writeValueAsString(newUserDto);
    }

    public static void registerUser(MockMvc mockMvc, ObjectMapper objectMapper, UserEntity user) throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRegisterUserRequestBody(mockMvc, objectMapper, user))).andReturn();
    }
}
