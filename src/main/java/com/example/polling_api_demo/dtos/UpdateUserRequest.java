package com.example.polling_api_demo.dtos;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private UserDTO userDTO;
    private String oldPassword;
    private String newPassword;
}
