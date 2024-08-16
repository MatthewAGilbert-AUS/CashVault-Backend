package team3.cashvault.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import team3.cashvault.TestDataUtil;
import team3.cashvault.domain.dto.LoginDto;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.services.FileUploadService;
import team3.cashvault.services.UserService;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {
    private final MockMvc mockMvc;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final FileUploadService fileUploadService;

    @Autowired
    public UserControllerIntegrationTests(MockMvc mockMvc, UserService userService, ObjectMapper objectMapper, FileUploadService fileUploadService) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.fileUploadService = fileUploadService;
    }

    @Test
    public void testThatLoggingInWithCorrectCredentialsRespondsHttp200OK() throws Exception {
        UserEntity testUserA = TestDataUtil.createTestUserEntityA();

        TestDataUtil.registerUser(mockMvc, objectMapper, testUserA);

        LoginDto loginDto = LoginDto.builder()
                .email(testUserA.getEmail())
                .hashedPassword(testUserA.getHashedPassword())
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testThatLoggingInWithCorrectCredentialsReturnsUser() throws Exception {
        UserEntity testUserA = TestDataUtil.createTestUserEntityA();
        TestDataUtil.registerUser(mockMvc, objectMapper, testUserA);
        Optional<UserEntity> userOptional = userService.findByEmail(testUserA.getEmail());


        LoginDto loginDto = LoginDto.builder()
                .email(testUserA.getEmail())
                .hashedPassword(testUserA.getHashedPassword())
                .build();

        if (userOptional.isPresent()) {
            testUserA = userOptional.get();
        }

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.user.email").value(testUserA.getEmail()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.user.firstName").value(testUserA.getFirstName()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.user.lastName").value(testUserA.getLastName()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.user.mobile").value(testUserA.getMobile()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.user.username").value(testUserA.getUsername()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.user.avatar").value(testUserA.getAvatar()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.user.profileInfo").value(testUserA.getProfileInfo()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.user.adminRole").value(testUserA.getAdminRole()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.user.creditCardNumber").value(testUserA.getCreditCardNumber()));
    }

    @Test
    public void testIncorrectLoginDetailsRespondsHttp401Unauthorized() throws Exception {
        LoginDto loginDto = LoginDto.builder()
                .email("incorrectEmail@domain.com")
                .hashedPassword("v3Ry$3cUR3")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testIncorrectLoginDetailsReturnsUserNotFoundMessage() throws Exception {
        LoginDto loginDto = LoginDto.builder()
                .email("incorrectEmail@domain.com")
                .hashedPassword("v3Ry$3cUR3")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @Test
    public void testThatAuthenticatingValidUserTokenRespondsWithHttp200OK() throws Exception {
        UserEntity testUserA = TestDataUtil.createTestUserEntityA();
        TestDataUtil.registerUser(mockMvc, objectMapper, testUserA);
        String token = TestDataUtil.getLoginToken(mockMvc, objectMapper, testUserA);

        Optional<UserEntity> userOptional = userService.findByEmail(testUserA.getEmail());

        if (userOptional.isPresent()) {
            testUserA = userOptional.get();
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/user/authenticate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(testUserA.getEmail()));
    }

    @Test
    public void testThatAuthenticatingValidUserTokenReturnsUser() throws Exception {
        UserEntity testUserA = TestDataUtil.createTestUserEntityA();
        TestDataUtil.registerUser(mockMvc, objectMapper, testUserA);
        String token = TestDataUtil.getLoginToken(mockMvc, objectMapper, testUserA);

        Optional<UserEntity> userOptional = userService.findByEmail(testUserA.getEmail());

        if (userOptional.isPresent()) {
            testUserA = userOptional.get();
        } else {
            assert false;
            return;
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/user/authenticate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userOptional.get().getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(userOptional.get().getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(userOptional.get().getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mobile").value(userOptional.get().getMobile()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(userOptional.get().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.avatar").value(userOptional.get().getAvatar()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profileInfo").value(userOptional.get().getProfileInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.adminRole").value(userOptional.get().getAdminRole()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.creditCardNumber").value(userOptional.get().getCreditCardNumber()));
    }

    @Test
    public void testAuthenticatingInvalidUserTokenRespondsHttp401Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/authenticate")
                        .header("Authorization", "Bearer " + "notAToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testAuthenticatingInvalidUserTokenReturnsInvalidToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/authenticate")
                        .header("Authorization", "Bearer " + "notAToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json("{\"error\": \"Invalid Token\"}"));
    }
}