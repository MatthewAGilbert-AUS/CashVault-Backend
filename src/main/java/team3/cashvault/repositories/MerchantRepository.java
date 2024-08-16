package team3.cashvault.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import team3.cashvault.domain.entities.MerchantEntity;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantEntity, Long> {
    Optional<MerchantEntity> findByToken(String token);

    Optional<MerchantEntity> findByBusinessName(String businessName);
}

