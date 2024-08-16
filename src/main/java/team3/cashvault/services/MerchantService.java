package team3.cashvault.services;

import java.util.List;
import java.util.Optional;

import team3.cashvault.domain.entities.MerchantEntity;

public interface MerchantService {
    Optional<MerchantEntity> findById(Long id);

    void createMerchant(MerchantEntity merchant);

    void deleteById(Long id);

    Optional<MerchantEntity> findByBusinessName(String businessName);

    Optional<MerchantEntity> findByToken(String token);

    List<MerchantEntity> getAllMerchants();

    void updateById(Long id);
}
