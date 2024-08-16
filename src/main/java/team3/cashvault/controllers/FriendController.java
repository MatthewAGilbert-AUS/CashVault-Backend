package team3.cashvault.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team3.cashvault.domain.dto.FriendDto;
import team3.cashvault.domain.entities.FriendEntity;
import team3.cashvault.domain.entities.UserEntity;
import team3.cashvault.services.FriendService;
import team3.cashvault.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
public class FriendController {
    private final UserService userService;
    private final FriendService friendService;
    private final KeyComponent key;

    public FriendController(UserService userService, FriendService friendService, KeyComponent key) {
        this.userService = userService;
        this.friendService = friendService;
        this.key = key;
    }


    public ResponseEntity<?> getAllUserFriends(@RequestHeader("Authorization") String token) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> adminUser = userService.findById(userId);
        if (adminUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Optional<List<FriendEntity>> friendsOptional = friendService.findAllByUserId(userId);

        if (friendsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No friends found for user");
        }

        List<FriendEntity> friends = friendsOptional.get();
        ArrayList<FriendDto> friendsDto = new ArrayList<FriendDto>();

        for (FriendEntity friend : friends) {
            FriendDto friendDto = FriendDto.builder()
                    .Id(friend.getId())
                    .firstName(friend.getFriend().getFirstName())
                    .lastName(friend.getFriend().getLastName())
                    .build();
            friendsDto.add(friendDto);
        }

        return ResponseEntity.status(HttpStatus.OK).body(friendsDto);
    }


    public ResponseEntity<?> addFriend(@RequestHeader("Authorization") String token, @RequestParam Long id) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Optional<List<FriendEntity>> friendsOptional = friendService.findAllByUserId(userId);

        if (friendsOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No friends found for user");
        }

        List<FriendEntity> friends = friendsOptional.get();

        for (FriendEntity friend : friends) {
            if (friend.getId() == id) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(id + " is already a friend.");
            }
        }

        Optional<UserEntity> friend = userService.findById(id);
        if (friend.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found for specified ID (" + id + ").");
        }

        FriendEntity friendEntity = FriendEntity.builder()
                .user(user.get())
                .friend(friend.get())
                .build();

        friendService.createFriendship(friendEntity);

        return ResponseEntity.status(HttpStatus.OK).body("Friend successfully added.");
    }


    public ResponseEntity<?> removeFriend(@RequestHeader("Authorization") String token, @RequestParam Long id) {
        // Validate token and extract user ID
        Long userId = ControllerUtil.validateTokenAndGetUserId(token, key.getKey());

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        Optional<UserEntity> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Optional<FriendEntity> friend = friendService.findById(id);

        if (friend.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friendship not found with id specified");
        }

        if (friend.get().getUser().getId() == userId) {
            friendService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Friend successfully removed.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Friendship does not belong to user id: " + userId);
        }
    }
}
