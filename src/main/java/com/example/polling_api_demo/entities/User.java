package com.example.polling_api_demo.entities;

import com.example.polling_api_demo.dtos.UserDTO;
import com.example.polling_api_demo.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserRole userRole;
    private Date resetPermittedAt;

    private boolean resetRequested = false;

    public UserDTO getUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setFirstName(firstName);
        userDTO.setLastName(lastName);
        userDTO.setEmail(email);
        userDTO.setUserRole(userRole);

        userDTO.setResetRequested(resetRequested);
        userDTO.setResetPermittedAt(resetPermittedAt);

        return userDTO;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
