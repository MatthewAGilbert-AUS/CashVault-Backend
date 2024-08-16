package team3.cashvault.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team3.cashvault.domain.entities.TransactionEntity;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    Optional<TransactionEntity> findByUserId(long userId);

    Optional<Iterable<TransactionEntity>> findAllByUserId(long userId);

    void deleteAllByUserId(long userId);
}
