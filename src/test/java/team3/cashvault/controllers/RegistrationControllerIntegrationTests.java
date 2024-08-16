package team3.cashvault.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import team3.cashvault.TestDataUtil;
import team3.cashvault.domain.dto.NewUserDto;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.services.FileUploadService;
import team3.cashvault.services.UserService;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class RegistrationControllerIntegrationTests {
    private final MockMvc mockMvc;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final FileUploadService fileUploadService;

    @Autowired
    public RegistrationControllerIntegrationTests(MockMvc mockMvc, UserService userService, ObjectMapper objectMapper, FileUploadService fileUploadService) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.fileUploadService = fileUploadService;
    }

    @Test
    public void testThatCreateUserSuccessfullyResponds201Created() throws Exception {
        UserEntity testUserA = TestDataUtil.createTestUserEntityA();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestDataUtil.createRegisterUserRequestBody(mockMvc, objectMapper, testUserA)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testThatCreateUserSuccessfullyReturnsUserCreatedSuccessfullyMessage() throws Exception {
        UserEntity testUserA = TestDataUtil.createTestUserEntityA();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestDataUtil.createRegisterUserRequestBody(mockMvc, objectMapper, testUserA))).andExpect(
                MockMvcResultMatchers.content().string("User registered successfully."));
    }

    @Test
    public void testRegisteringAccountWithANonExampleRespondsHttp400BadRequest() throws Exception {
        UserEntity testUserA = TestDataUtil.createTestUserEntityA();
        NewUserDto newUserDto = NewUserDto.builder()
                .email("invalid email breaking regex requirements")
                .firstName(testUserA.getFirstName())
                .lastName(testUserA.getLastName())
                .mobile(testUserA.getMobile())
                .username(testUserA.getUsername())
                .hashedPassword(testUserA.getHashedPassword())
                .creditCardNumber(testUserA.getCreditCardNumber())
                .dob("1990-01-01")
                .build();


        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisteringAccountWithANonExampleReturnsInvalidEmail() throws Exception {
        UserEntity testUserA = TestDataUtil.createTestUserEntityA();
        NewUserDto newUserDto = NewUserDto.builder()
                .email("invalid email breaking regex requirements")
                .firstName(testUserA.getFirstName())
                .lastName(testUserA.getLastName())
                .mobile(testUserA.getMobile())
                .username(testUserA.getUsername())
                .hashedPassword(testUserA.getHashedPassword())
                .creditCardNumber(testUserA.getCreditCardNumber())
                .dob("1990-01-01")
                .build();


        mockMvc.perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(MockMvcResultMatchers.content().string("Invalid email address."));
    }

    @Test
    public void testRegisteringWithADuplicateEmailRespondsHttp409Conflict() throws Exception {
        UserEntity testUserA = TestDataUtil.createTestUserEntityA();
        UserEntity testUserB = TestDataUtil.createTestUserEntityB();
        TestDataUtil.registerUser(mockMvc, objectMapper, testUserB);

        NewUserDto newUserDto = NewUserDto.builder()
                .email(testUserB.getEmail())
                .firstName(testUserA.getFirstName())
                .lastName(testUserA.getLastName())
                .mobile(testUserA.getMobile())
                .username(testUserA.getUsername())
                .hashedPassword(testUserA.getHashedPassword())
                .creditCardNumber(testUserA.getCreditCardNumber())
                .dob("1990-01-01")
                .build();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new String("{\"email\":\"" + testUserB.getEmail() + "\"}"))
        ).andExpect(status().isConflict());
    }

    @Test
    public void testRegisteringWithADuplicateEmailReturnsEmailAlreadyRegistered() throws Exception {
        UserEntity testUserA = TestDataUtil.createTestUserEntityA();
        UserEntity testUserB = TestDataUtil.createTestUserEntityB();
        TestDataUtil.registerUser(mockMvc, objectMapper, testUserB);

        NewUserDto newUserDto = NewUserDto.builder()
                .email(testUserB.getEmail())
                .firstName(testUserA.getFirstName())
                .lastName(testUserA.getLastName())
                .mobile(testUserA.getMobile())
                .username(testUserA.getUsername())
                .hashedPassword(testUserA.getHashedPassword())
                .creditCardNumber(testUserA.getCreditCardNumber())
                .dob("1990-01-01")
                .build();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/user/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new String("{\"email\":\"" + testUserB.getEmail() + "\"}"))
        ).andExpect(MockMvcResultMatchers.content().string("Email is already registered."));
    }
}