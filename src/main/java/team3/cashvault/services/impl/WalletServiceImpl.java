package team3.cashvault.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team3.cashvault.domain.entities.WalletEntity;
import team3.cashvault.repositories.WalletRepository;
import team3.cashvault.services.WalletService;

import java.util.Optional;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void createWallet(WalletEntity walletEntity) {
        walletRepository.save(walletEntity);
    }

    @Override
    public Optional<WalletEntity> findById(long id) {
        return walletRepository.findById(id);
    }

    @Override
    public Optional<WalletEntity> findByUserId(long id) {
        return walletRepository.findByUserId(id);
    }

    @Override
    public void updateWallet(WalletEntity walletEntity) {
        walletRepository.save(walletEntity);

    }
}
