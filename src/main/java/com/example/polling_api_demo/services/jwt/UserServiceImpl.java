package com.example.polling_api_demo.services.jwt;

import com.example.polling_api_demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
