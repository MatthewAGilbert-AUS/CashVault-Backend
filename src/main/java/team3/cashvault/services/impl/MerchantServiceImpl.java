package team3.cashvault.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team3.cashvault.domain.entities.MerchantEntity;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.repositories.MerchantRepository;
import team3.cashvault.services.MerchantService;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;


    public MerchantServiceImpl(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;

    }


    @Override
    public Optional<MerchantEntity> findById(Long id) {
        return merchantRepository.findById(id);
    }

    @Override
    public void createMerchant(MerchantEntity merchant) {
        merchantRepository.save(merchant);
    }

    @Override
    public void deleteById(Long id) {
        merchantRepository.deleteById(id);
    }

    @Override
    public Optional<MerchantEntity> findByBusinessName(String businessName) {
        return merchantRepository.findByBusinessName(businessName);
    }


    @Override
    public Optional<MerchantEntity> findByToken(String token) {
        return merchantRepository.findByToken(token);

    }

    @Override
    public List<MerchantEntity> getAllMerchants() {
        return merchantRepository.findAll();
    }

    @Override
    public void updateById(Long id) {
        Optional<MerchantEntity> merchant = merchantRepository.findById(id);
        merchant.ifPresent(merchantRepository::save);
    }
}
