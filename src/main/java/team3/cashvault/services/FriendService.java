package team3.cashvault.services;

import team3.cashvault.domain.entities.FriendEntity;

import java.util.List;
import java.util.Optional;

public interface FriendService {

    void createFriendship(FriendEntity friendEntity);
    Optional<FriendEntity> findById(long id);
    Optional<List<FriendEntity>> findAllByUserId(long userId);
    Optional<List<FriendEntity>> findAllByFriendId(long friendId);
    void deleteById(long id);

}
