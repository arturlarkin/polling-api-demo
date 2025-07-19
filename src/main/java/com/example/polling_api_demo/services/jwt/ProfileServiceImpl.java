package com.example.polling_api_demo.services.jwt;

import com.example.polling_api_demo.dtos.UpdateUserRequest;
import com.example.polling_api_demo.dtos.UserDTO;
import com.example.polling_api_demo.entities.User;
import com.example.polling_api_demo.repositories.UserRepository;
import com.example.polling_api_demo.utils.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final AuthHelper authHelper;
    private final PasswordEncoder passwordEncoder;

    public UserDTO getCurrentUser() {
        User user = authHelper.getLoggedInUser();
        return user.getUserDTO();
    }

    public UserDTO updateUser(UpdateUserRequest request) {
        User user = authHelper.getLoggedInUser();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        UserDTO dto = request.getUserDTO();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getOldPassword() == null || !passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new BadCredentialsException("Old password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return userRepository.save(user).getUserDTO();
    }

    public void deleteCurrentUser() {
        User user = authHelper.getLoggedInUser();
        userRepository.deleteById(user.getId());
    }
}
