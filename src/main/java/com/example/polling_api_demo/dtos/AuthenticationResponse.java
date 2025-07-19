package com.example.polling_api_demo.dtos;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String jwtToken;
    private String name;
}
