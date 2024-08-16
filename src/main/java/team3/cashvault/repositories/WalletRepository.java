package team3.cashvault.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import team3.cashvault.domain.entities.WalletEntity;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    Optional<WalletEntity> findByUserId(long userId);

    void deleteByUserId(long userId);
}
