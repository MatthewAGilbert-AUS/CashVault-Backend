package team3.cashvault.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import team3.cashvault.domain.dto.UpdateUserDto;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.domain.entities.WalletEntity;
import team3.cashvault.repositories.TransactionRepository;
import team3.cashvault.repositories.UserRepository;
import team3.cashvault.repositories.WalletRepository;
import team3.cashvault.services.UserService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;


    public UserServiceImpl(UserRepository userRepository, WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void createUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void updateUserDetails(Long userId, UpdateUserDto updates) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(user -> {
            if (updates.getFirstName() != null) {
                user.setFirstName(updates.getFirstName());
            }
            if (updates.getLastName() != null) {
                user.setLastName(updates.getLastName());
            }
            if (updates.getMobile() != null) {
                user.setMobile(updates.getMobile());
            }
            if (updates.getUsername() != null) {
                user.setUsername(updates.getUsername());
            }
            if (updates.getAvatar() != null) {
                user.setAvatar(updates.getAvatar());
            }
            if (updates.getProfileInfo() != null) {
                user.setProfileInfo(updates.getProfileInfo());
            }
            userRepository.save(user);
        });

    }

    @Override
    public void updateUserDetailsAdmin(String email, UpdateUserDto updates) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        optionalUser.ifPresent(user -> {
            if (updates.getFirstName() != null) {
                user.setFirstName(updates.getFirstName());
            }
            if (updates.getLastName() != null) {
                user.setLastName(updates.getLastName());
            }
            if (updates.getMobile() != null) {
                user.setMobile(updates.getMobile());
            }
            if (updates.getUsername() != null) {
                user.setUsername(updates.getUsername());
            }
            if (updates.getAvatar() != null) {
                user.setAvatar(updates.getAvatar());
            }
            if (updates.getProfileInfo() != null) {
                user.setProfileInfo(updates.getProfileInfo());
            }

            userRepository.save(user);
        });

    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(user -> {
            user.setAvatar(avatarUrl);
            userRepository.save(user);
        });
    }

    @Override
    public void updateUserPassword(Long userId, String password) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(user -> {
            user.setHashedPassword(password);
            userRepository.save(user);
        });
    }

    @Override
    public void updateUserEmail(Long userId, String email) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(user -> {
            user.setEmail(email);
            userRepository.save(user);
        });
    }

    @Override
    public void updateCreditCard(Long userId, String creditCardNumber) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(user -> {
            user.setCreditCardNumber(creditCardNumber);
            userRepository.save(user);
        });
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserByEmail(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        userOptional.ifPresent(userRepository::delete);

    }

    @Override
    public void deleteById(long id) {
        transactionRepository.deleteAllByUserId(id);
        walletRepository.deleteByUserId(id);
        userRepository.deleteById(id);

    }

    @Override
    public void updateAdminRole(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        userOptional.ifPresent(user -> {
            user.setAdminRole(!user.getAdminRole());
            userRepository.save(user);
        });

    }

    @Override
    public void updateUserStatus(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        userOptional.ifPresent(user -> {
            user.setActive(!user.getActive());
            userRepository.save(user);
        });
    }

    @Override
    public void updateById(Long id) {
        Optional<UserEntity> user = userRepository.findById(id);
        user.ifPresent(userRepository::save);
    }
}
