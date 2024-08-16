package team3.cashvault.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import team3.cashvault.controllers.ControllerUtil;
import team3.cashvault.domain.entities.UserEntity;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService (UserService userService){
        this.userService = userService;
    }

    public boolean authenticateUser(String email, String password) {
        Optional<UserEntity> foundLogin = userService.findByEmail(email);
        if (foundLogin.isPresent()) {
            UserEntity user = foundLogin.get();
            if (!user.getActive()) {
                return false;
            } else {
                if (ControllerUtil.hashPassword(password).equals(user.getHashedPassword())) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

}
