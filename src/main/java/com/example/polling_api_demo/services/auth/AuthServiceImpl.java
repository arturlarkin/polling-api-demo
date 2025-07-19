package com.example.polling_api_demo.services.auth;

import com.example.polling_api_demo.dtos.SignupRequest;
import com.example.polling_api_demo.dtos.UserDTO;
import com.example.polling_api_demo.entities.User;
import com.example.polling_api_demo.enums.UserRole;
import com.example.polling_api_demo.repositories.UserRepository;
import com.example.polling_api_demo.utils.MailUtil;
import com.example.polling_api_demo.utils.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailUtil mailUtil;

    @Value("${app.max.users}")
    private int maxUsers;

    @Value("${app.reset.delay}")
    private int resetDelay;

    @Override
    public UserDTO createUser(SignupRequest signupRequest) {
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setUserRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setResetRequested(false);
        user.setResetPermittedAt(null);
        User createdUser = userRepository.save(user);

        return createdUser.getUserDTO();
    }

    @Override
    public Boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }

    @Override
    public UserDTO resetPassword(String email) {
        User user = userRepository.findFirstByEmail(email).get();
        String newPassword = PasswordGenerator.generatePassword();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        mailUtil.resetPasswordMessage(user, newPassword);

        return user.getUserDTO();
    }

    @Override
    public Boolean hasMaxUsers() {
        return userRepository.count() >= maxUsers;
    }

    @Override
    public Boolean isResetPermitted(String email) {
        User user = userRepository.findFirstByEmail(email).get();
        if (!user.isResetRequested()) {
            Date permissionDate = createPermissionDate();

            user.setResetRequested(true);
            user.setResetPermittedAt(permissionDate);
            userRepository.save(user);

            return true;
        } else {
            if (!user.getResetPermittedAt().before(new Date())) {
                return false;
            } else {
                Date permissionDate = createPermissionDate();
                user.setResetPermittedAt(permissionDate);
                userRepository.save(user);

                return true;
            }
        }
    }

    private Date createPermissionDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, resetDelay);

        return calendar.getTime();
    }
}
