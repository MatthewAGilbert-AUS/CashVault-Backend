package team3.cashvault.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserDto {
   
    private String email;
    private String firstName;
    private String lastName;
    private String mobile;
    private String username;
    private String hashedPassword;
    private String verificationToken;
    private String creditCardNumber;
    private String dob;
}
