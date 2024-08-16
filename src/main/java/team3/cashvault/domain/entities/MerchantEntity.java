package team3.cashvault.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Merchants")
public class MerchantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "merchant_id_seq")
    @SequenceGenerator(name = "merchant_id_seq", sequenceName = "merchant_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "business_name", nullable = false, unique = true)
    private String businessName;

    @Column(name = "country", nullable = false)
    private String country;

    
    @Column(name = "currency", nullable = false)
    private String currency;

    
    @Column(name = "status", nullable = false)
    private Boolean status;

    
    @Column(name = "token", nullable = true)
    private String token;


}
