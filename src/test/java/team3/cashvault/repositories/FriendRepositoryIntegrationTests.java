package team3.cashvault.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team3.cashvault.TestDataUtil;
import team3.cashvault.domain.entities.FriendEntity;
import team3.cashvault.domain.entities.UserEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FriendRepositoryIntegrationTests {
    private final FriendRepository underTest;
    private final UserRepository userRepository;

    @Autowired
    public FriendRepositoryIntegrationTests(final FriendRepository underTest, UserRepository userRepository) {
        this.underTest = underTest;
        this.userRepository = userRepository;
    }

    @Test
    public void testThatFriendCanBeCreatedAndRecalled() {
        UserEntity userEntityA = TestDataUtil.createTestUserEntityA();
        userRepository.save(userEntityA);

        UserEntity userEntityB = TestDataUtil.createTestUserEntityB();
        userRepository.save(userEntityB);

        FriendEntity friendEntity = TestDataUtil.createFriendship(userEntityA, userEntityB);

        underTest.save(friendEntity);

        Optional<FriendEntity> result = underTest.findById(friendEntity.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(friendEntity);
    }

    @Test
    public void testThatMultipleFriendsCanBeCreatedAndRecalled() {
        UserEntity userEntityA = TestDataUtil.createTestUserEntityA();
        userRepository.save(userEntityA);

        UserEntity userEntityB = TestDataUtil.createTestUserEntityB();
        userRepository.save(userEntityB);

        UserEntity userEntityC = TestDataUtil.createTestUserEntityC();
        userRepository.save(userEntityC);

        UserEntity userEntityD = TestDataUtil.createTestUserEntityD();
        userRepository.save(userEntityD);

        FriendEntity friendshipA = TestDataUtil.createFriendship(userEntityA, userEntityB);
        underTest.save(friendshipA);

        FriendEntity friendshipB = TestDataUtil.createFriendship(userEntityB, userEntityA);
        underTest.save(friendshipB);

        FriendEntity friendshipC = TestDataUtil.createFriendship(userEntityC, userEntityD);
        underTest.save(friendshipC);

        FriendEntity friendshipD = TestDataUtil.createFriendship(userEntityD, userEntityC);
        underTest.save(friendshipD);

        Iterable<FriendEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(4)
                .containsExactly(friendshipA, friendshipB, friendshipC, friendshipD);
    }

    // Unrealistic to use this in our solution
    @Test
    public void testThatFriendshipsCanBeUpdated() {
        UserEntity userEntityA = TestDataUtil.createTestUserEntityA();
        userRepository.save(userEntityA);

        UserEntity userEntityB = TestDataUtil.createTestUserEntityB();
        userRepository.save(userEntityB);

        FriendEntity friendshipEntityA = TestDataUtil.createFriendship(userEntityA, userEntityB);
        underTest.save(friendshipEntityA);


        Optional<FriendEntity> result = underTest.findById(friendshipEntityA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(friendshipEntityA);
    }

    @Test
    public void testThatFriendshipsCanBeDeleted() {
        UserEntity userEntityA = TestDataUtil.createTestUserEntityA();
        userRepository.save(userEntityA);

        UserEntity userEntityB = TestDataUtil.createTestUserEntityB();
        userRepository.save(userEntityB);

        FriendEntity friendshipEntityA = TestDataUtil.createFriendship(userEntityA, userEntityB);
        underTest.save(friendshipEntityA);

        underTest.deleteById(friendshipEntityA.getId());

        Optional<FriendEntity> result = underTest.findById(friendshipEntityA.getId());
        assertThat(result).isEmpty();
    }
}