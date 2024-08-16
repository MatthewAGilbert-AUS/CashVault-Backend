package team3.cashvault.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team3.cashvault.TestDataUtil;
import team3.cashvault.domain.entities.UserEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryIntegrationTests {
    private final UserRepository underTest;

    @Autowired
    public UserRepositoryIntegrationTests(final UserRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatUserCanBeCreatedAndRecalled() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityA();
        underTest.save(userEntity);
        Optional<UserEntity> result = underTest.findById(userEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userEntity);
    }

    @Test
    public void testThatMultipleUsersCanBeCreatedAndRecalled() {
        UserEntity userEntityA = TestDataUtil.createTestUserEntityA();
        underTest.save(userEntityA);
        UserEntity userEntityB = TestDataUtil.createTestUserEntityB();
        underTest.save(userEntityB);
        UserEntity userEntityC = TestDataUtil.createTestUserEntityC();
        underTest.save(userEntityC);
        UserEntity userEntityD = TestDataUtil.createTestUserEntityD();
        underTest.save(userEntityD);

        Iterable<UserEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(4).
                containsExactly(userEntityA, userEntityB, userEntityC, userEntityD);
    }

    @Test
    public void testThatUserCanBeUpdated() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityA();
        underTest.save(userEntity);
        userEntity.setFirstName("Changed");
        underTest.save(userEntity);
        Optional<UserEntity> result = underTest.findById(userEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userEntity);
    }

    @Test
    public void testThatUserCanBeDeleted() {
        UserEntity userEntity = TestDataUtil.createTestUserEntityA();
        underTest.save(userEntity);
        underTest.deleteById(userEntity.getId());
        Optional<UserEntity> result = underTest.findById(userEntity.getId());
        assertThat(result).isEmpty();
    }


}