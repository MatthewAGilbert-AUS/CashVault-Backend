package team3.cashvault.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team3.cashvault.TestDataUtil;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.domain.entities.WalletEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WalletRepositoryIntegrationTests {

    private final WalletRepository underTest;

    @Autowired
    public WalletRepositoryIntegrationTests(final WalletRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatWalletCanBeCreatedAndRecalled() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityA();
        WalletEntity walletEntity = TestDataUtil.createTestWalletEntity(userEntity);
        underTest.save(walletEntity);
        Optional<WalletEntity> result = underTest.findById(walletEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(walletEntity);
    }

    @Test
    public void testThatMultipleWalletsCanBeCreatedAndRecalled() {
        UserEntity userEntityA = TestDataUtil.createTestUserEntityA();
        UserEntity userEntityB = TestDataUtil.createTestUserEntityB();
        UserEntity userEntityC = TestDataUtil.createTestUserEntityC();
        UserEntity userEntityD = TestDataUtil.createTestUserEntityD();

        WalletEntity walletEntityA = TestDataUtil.createTestWalletEntity(userEntityA);
        underTest.save(walletEntityA);

        WalletEntity walletEntityB = TestDataUtil.createTestWalletEntity(userEntityB);
        underTest.save(walletEntityB);

        WalletEntity walletEntityC = TestDataUtil.createTestWalletEntity(userEntityC);
        underTest.save(walletEntityC);

        WalletEntity walletEntityD = TestDataUtil.createTestWalletEntity(userEntityD);
        underTest.save(walletEntityD);

        Iterable<WalletEntity> result = underTest.findAll();

        assertThat(result)
                .hasSize(4)
                .containsExactly(walletEntityA, walletEntityB, walletEntityC, walletEntityD);
    }

    @Test
    public void testThatWalletCanBeUpdated() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityA();
        WalletEntity walletEntity = TestDataUtil.createTestWalletEntity(userEntity);
        underTest.save(walletEntity);

        walletEntity.setBalance(new BigDecimal("10000.00"));
        underTest.save(walletEntity);

        Optional<WalletEntity> result = underTest.findById(walletEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(walletEntity);
    }

    @Test
    public void testThatWalletCanBeDeleted() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityA();
        WalletEntity walletEntity = TestDataUtil.createTestWalletEntity(userEntity);
        underTest.save(walletEntity);

        underTest.deleteById(walletEntity.getId());

        Optional<WalletEntity> result = underTest.findById(walletEntity.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatWalletCanBeRecalledByUserId() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityA();
        WalletEntity walletEntity = TestDataUtil.createTestWalletEntity(userEntity);
        underTest.save(walletEntity);
        Optional<WalletEntity> result = underTest.findByUserId(userEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(walletEntity);
    }

}
