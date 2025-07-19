package com.example.polling_api_demo.services.auth;

import com.example.polling_api_demo.dtos.SignupRequest;
import com.example.polling_api_demo.dtos.UserDTO;

public interface AuthService {
    UserDTO createUser(SignupRequest signupRequest);

    Boolean hasUserWithEmail(String email);

    UserDTO resetPassword(String email);

    Boolean hasMaxUsers();

    Boolean isResetPermitted(String email);
}
