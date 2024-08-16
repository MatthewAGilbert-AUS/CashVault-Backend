package team3.cashvault.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team3.cashvault.domain.entities.FriendEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, Long> {
    Optional<List<FriendEntity>> findAllByUserId(long userId);

    Optional<List<FriendEntity>> findAllByFriendId(long friendId);
}
