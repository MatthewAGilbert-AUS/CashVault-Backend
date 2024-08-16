package team3.cashvault.services;

import team3.cashvault.domain.entities.WalletEntity;

import java.util.Optional;

public interface WalletService {
    void createWallet(WalletEntity walletEntity);
    Optional<WalletEntity> findById(long id);
    Optional<WalletEntity> findByUserId(long id);
    void updateWallet(WalletEntity walletEntity);
}
