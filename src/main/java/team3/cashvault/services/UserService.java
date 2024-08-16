package team3.cashvault.services;

import java.util.List;
import java.util.Optional;

import team3.cashvault.domain.dto.UpdateUserDto;

import team3.cashvault.domain.entities.UserEntity;

public interface UserService {

    void createUser(UserEntity userEntity);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findById(Long id);

    void updateUserDetails(Long userId, UpdateUserDto updates);

    void updateAvatar(Long userId, String avatarUrl);

    List<UserEntity> getAllUsers();

    void updateUserPassword(Long userId, String password);

    void updateCreditCard(Long userId, String creditCardNumber);

    void deleteUserByEmail(String email);

    void deleteById(long id);

    void updateUserEmail(Long userId, String email);

    void updateUserDetailsAdmin(String email, UpdateUserDto updates);

    void updateAdminRole(String email);

    void updateUserStatus(String email);

    void updateById(Long id);
}
