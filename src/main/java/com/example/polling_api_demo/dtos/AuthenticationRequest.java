package com.example.polling_api_demo.dtos;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String email;
    private String password;
}
