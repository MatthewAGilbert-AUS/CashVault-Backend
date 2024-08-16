package team3.cashvault.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Wallets")
public class WalletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_id_seq")
    @SequenceGenerator(name = "wallet_id_seq", sequenceName = "wallet_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
