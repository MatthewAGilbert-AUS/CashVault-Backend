package team3.cashvault.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team3.cashvault.domain.entities.FriendEntity;
import team3.cashvault.domain.entities.WalletEntity;
import team3.cashvault.repositories.FriendRepository;
import team3.cashvault.repositories.WalletRepository;
import team3.cashvault.services.FriendService;
import team3.cashvault.services.WalletService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendServiceImpl implements FriendService {
    private final FriendRepository friendRepository;

    public FriendServiceImpl(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }
    @Override
    public void createFriendship(FriendEntity friendEntity) {
        this.friendRepository.save(friendEntity);
    }
    @Override
    public Optional<FriendEntity> findById(long id) {
        return friendRepository.findById(id);
    }
    @Override
    public Optional<List<FriendEntity>> findAllByUserId(long userId) {
        return friendRepository.findAllByUserId(userId);
    }
    @Override
    public Optional<List<FriendEntity>> findAllByFriendId(long friendId) {
        return friendRepository.findAllByUserId(friendId);
    }

    @Override
    public void deleteById(long id) {
        friendRepository.deleteById(id);
    }
}
