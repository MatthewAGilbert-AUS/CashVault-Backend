package team3.cashvault.domain.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team3.cashvault.domain.TransactionType;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionReturnDto {
    private Long id;
    private String currency;
    private LocalDateTime transactionTime;
    private BigDecimal amount;
    private Long destinationId;
    private Long merchantId;
    private String biller;
    private String destination;

    @Enumerated(EnumType.ORDINAL)
    private TransactionType transactionType;
}
