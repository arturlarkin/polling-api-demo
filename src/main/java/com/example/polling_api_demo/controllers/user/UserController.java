package com.example.polling_api_demo.controllers.user;

import com.example.polling_api_demo.dtos.UpdateUserRequest;
import com.example.polling_api_demo.dtos.UserDTO;
import com.example.polling_api_demo.services.jwt.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserProfile() {
        return ResponseEntity.ok(profileService.getCurrentUser());
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUserProfile(
            @RequestBody UpdateUserRequest request) {
        try {
            UserDTO updated = profileService.updateUser(request);
            return ResponseEntity.ok(updated);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Update failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount() {
        profileService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}
