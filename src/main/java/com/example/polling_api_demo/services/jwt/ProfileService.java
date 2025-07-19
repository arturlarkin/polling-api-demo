package com.example.polling_api_demo.services.jwt;

import com.example.polling_api_demo.dtos.UpdateUserRequest;
import com.example.polling_api_demo.dtos.UserDTO;

public interface ProfileService {
    UserDTO getCurrentUser();
    UserDTO updateUser(UpdateUserRequest request);
    void deleteCurrentUser();
}
