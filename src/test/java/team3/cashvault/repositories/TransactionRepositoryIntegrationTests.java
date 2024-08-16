package team3.cashvault.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team3.cashvault.TestDataUtil;
import team3.cashvault.domain.entities.TransactionEntity;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.domain.entities.WalletEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionRepositoryIntegrationTests {
    private final TransactionRepository underTest;
    private final UserRepository userRepository;

    @Autowired
    public TransactionRepositoryIntegrationTests(final TransactionRepository underTest, UserRepository userRepository) {
        this.underTest = underTest;
        this.userRepository = userRepository;
    }

    @Test
    public void testThatTransactionCanBeCreatedAndRecalled() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityB();
        userRepository.save(userEntity);

        TransactionEntity transactionEntity = TestDataUtil.createTransactionEntityB(userEntity);
        underTest.save(transactionEntity);

        Optional<TransactionEntity> result = underTest.findById(transactionEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(transactionEntity);
    }

    @Test
    public void testThatMultipleTransactionsCanBeCreatedAndRecalled() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityA();
        userRepository.save(userEntity);

        TransactionEntity transactionEntityA = TestDataUtil.createTransactionEntityA(userEntity);
        underTest.save(transactionEntityA);

        TransactionEntity transactionEntityB = TestDataUtil.createTransactionEntityB(userEntity);
        underTest.save(transactionEntityB);

        TransactionEntity transactionEntityC = TestDataUtil.createTransactionEntityC(userEntity);
        underTest.save(transactionEntityC);

        Iterable<TransactionEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(transactionEntityA, transactionEntityB, transactionEntityC);
    }

    @Test
    public void testThatTransactionCanBeUpdated() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityB();
        userRepository.save(userEntity);

        TransactionEntity transactionEntity = TestDataUtil.createTransactionEntityB(userEntity);
        underTest.save(transactionEntity);

        transactionEntity.setAmount(new BigDecimal("400.50"));
        underTest.save(transactionEntity);

        Optional<TransactionEntity> result = underTest.findById(transactionEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(transactionEntity);
    }

    @Test
    public void testThatTransactionCanBeDeleted() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityB();
        userRepository.save(userEntity);

        TransactionEntity transactionEntity = TestDataUtil.createTransactionEntityB(userEntity);
        underTest.save(transactionEntity);

        underTest.deleteById(transactionEntity.getId());

        Optional<TransactionEntity> result = underTest.findById(transactionEntity.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatTransactionCanBeRecalledByUserId() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityB();
        userRepository.save(userEntity);

        TransactionEntity transactionEntity = TestDataUtil.createTransactionEntityB(userEntity);
        underTest.save(transactionEntity);

        Optional<TransactionEntity> result = underTest.findByUserId(userEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(transactionEntity);
    }

    @Test
    public void testThatMultipleTransactionCanBeRecalledByUserId() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityA();
        userRepository.save(userEntity);

        TransactionEntity transactionEntityA = TestDataUtil.createTransactionEntityA(userEntity);
        underTest.save(transactionEntityA);

        TransactionEntity transactionEntityB = TestDataUtil.createTransactionEntityB(userEntity);
        underTest.save(transactionEntityB);

        TransactionEntity transactionEntityC = TestDataUtil.createTransactionEntityC(userEntity);
        underTest.save(transactionEntityC);

        Iterable<TransactionEntity> result = underTest.findAllByUserId(userEntity.getId()).get();
        assertThat(result)
                .hasSize(3).
                containsExactly(transactionEntityA, transactionEntityB, transactionEntityC);
    }
    
}