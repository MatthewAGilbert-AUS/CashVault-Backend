// package team3.cashvault.controllers;


// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.context.junit.jupiter.SpringExtension;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
// import team3.cashvault.TestDataUtil;
// import team3.cashvault.domain.entities.UserEntity;
// import team3.cashvault.repositories.UserRepository;

// import java.math.BigDecimal;
// import java.util.Optional;

// @SpringBootTest
// @ExtendWith(SpringExtension.class)
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// @AutoConfigureMockMvc
// public class WalletControllerIntegrationTests {

//     private MockMvc mockMvc;
//     private ObjectMapper objectMapper;
//     private UserRepository userRepository;

//     @Autowired
//     public WalletControllerIntegrationTests(MockMvc mockMvc, UserRepository userRepository) {
//         this.mockMvc = mockMvc;
//         this.userRepository = userRepository;
//         this.objectMapper =  new ObjectMapper();
//     }

//     @Test
//     public void testThatGetWalletAmountReturnsHttp200OK() throws Exception {
//         UserEntity testUserA = TestDataUtil.createTestUserEntityA();

//         String token = TestDataUtil.getTestLoginToken(testUserA, objectMapper, mockMvc);

//         mockMvc.perform(
//                 MockMvcRequestBuilders.post("/user/wallet/amount")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .header("authorization", token)
//         ).andExpect(MockMvcResultMatchers.status().isOk());
//     }

//     @Test
//     public void testThatIncreaseWalletAmountReturnsHttp200OK() throws Exception {
//         UserEntity testUserA = TestDataUtil.createTestUserEntityA();

//         String token = TestDataUtil.getTestLoginToken(testUserA, objectMapper, mockMvc);

//         String walletJson = "{\"amount\": \"40.20\"}";

//         mockMvc.perform(
//                 MockMvcRequestBuilders.post("/user/wallet/increase")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .header("authorization", token)
//                         .content(walletJson)
//         ).andExpect(MockMvcResultMatchers.status().isOk());
//     }

//     @Test
//     public void testThatIncreasingWalletAmountReturnsNewValue() throws  Exception{
//         UserEntity testUserA = TestDataUtil.createTestUserEntityA();

//         String token = TestDataUtil.getTestLoginToken(testUserA, objectMapper, mockMvc);

//         String walletJson = "{\"amount\": \"40.20\"}";

//         mockMvc.perform(
//                 MockMvcRequestBuilders.post("/user/wallet/increase")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .header("authorization", token)
//                         .content(walletJson)
//         ).andExpect(MockMvcResultMatchers.content().string("Added funds, new balance: 40.20"));
//     }

// }
