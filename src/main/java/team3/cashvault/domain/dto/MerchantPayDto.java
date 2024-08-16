package team3.cashvault.domain.dto;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantPayDto {
    private String email;
    private BigDecimal amount;
    private String password;
    private String token;
}
