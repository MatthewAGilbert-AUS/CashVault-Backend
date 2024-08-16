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
@Table(name = "Friends")
public class FriendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friend_id_seq")
    @SequenceGenerator(name = "friend_id_seq", sequenceName = "friend_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne()
    @JoinColumn(name = "friend_id", nullable = false)
    private UserEntity friend;
    
}
