package com.example.polling_api_demo.dtos;

import com.example.polling_api_demo.enums.UserRole;
import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole userRole;
    private Date resetPermittedAt;
    private boolean resetRequested;
}
