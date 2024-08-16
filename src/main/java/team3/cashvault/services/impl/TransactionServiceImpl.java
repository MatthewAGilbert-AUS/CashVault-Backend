package team3.cashvault.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team3.cashvault.domain.entities.TransactionEntity;
import team3.cashvault.repositories.TransactionRepository;
import team3.cashvault.services.TransactionService;

import java.util.Optional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void createTransaction(TransactionEntity transactionEntity) {
        this.transactionRepository.save(transactionEntity);
    }

    @Override
    public Optional<TransactionEntity> findById(long id) {
        return this.transactionRepository.findById(id);
    }

    @Override
    public Optional<TransactionEntity> findByUserId(long id) {
        return this.transactionRepository.findByUserId(id);
    }

    @Override
    public Optional<Iterable<TransactionEntity>> findAllByUserId(long userId) {
        return this.transactionRepository.findAllByUserId(userId);
    }

    @Override
    public void deleteByUserId(long userId) {
        transactionRepository.deleteAllByUserId(userId);
    }

    @Override
    public void updateTransaction(TransactionEntity transactionEntity) {
        this.transactionRepository.save(transactionEntity);
    }

    @Override
    public void updateConnectedTransactions(long deletedId) {

    }
}
