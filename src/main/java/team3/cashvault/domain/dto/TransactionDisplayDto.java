package team3.cashvault.domain.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team3.cashvault.domain.TransactionType;


import java.math.BigDecimal;

//only adding this to keep the id away from sending to the front
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDisplayDto {
    private String name;
    private String currency;
    private String transactionTime;
    private BigDecimal amount;
    private String destination;
    private BigDecimal fees;

    @Enumerated(EnumType.ORDINAL)
    private TransactionType transactionType;
}
