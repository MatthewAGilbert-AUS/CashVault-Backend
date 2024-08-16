package team3.cashvault.services;

import team3.cashvault.domain.entities.TransactionEntity;

import java.util.Optional;

public interface TransactionService {

    void createTransaction(TransactionEntity transactionEntity);

    Optional<TransactionEntity> findById(long id);
    Optional<TransactionEntity> findByUserId(long id);
    Optional<Iterable<TransactionEntity>> findAllByUserId(long userId);
    void deleteByUserId(long userId);
    void updateTransaction(TransactionEntity transactionEntity);
    void updateConnectedTransactions(long deletedId);
}
