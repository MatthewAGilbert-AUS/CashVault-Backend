package team3.cashvault.domain.entities;

import jakarta.persistence.*;
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
@Entity
@Table(name = "Transactions")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_seq")
    @SequenceGenerator(name = "transaction_id_seq", sequenceName = "transaction_id_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    private Long destinationId;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "biller_name")
    private String biller;

    @Column(name = "merchant_id")
    private Long merchantId;

}
