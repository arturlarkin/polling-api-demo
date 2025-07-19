package com.example.polling_api_demo.dtos;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
