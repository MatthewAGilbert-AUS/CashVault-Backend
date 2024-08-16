package team3.cashvault.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserAdminDto {

    private String email;
    private String firstName;
    private String lastName;
    private String mobile;
    private String username;
    private String avatar;
    private String profileInfo;
    private Boolean adminRole;
    private String creditCardNumber;
    private String hashedPassword;
    private String newEmail;
    private Boolean active;

}
