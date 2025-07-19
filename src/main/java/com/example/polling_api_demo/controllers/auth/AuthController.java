package com.example.polling_api_demo.controllers.auth;

import com.example.polling_api_demo.dtos.*;
import com.example.polling_api_demo.entities.User;
import com.example.polling_api_demo.repositories.UserRepository;
import com.example.polling_api_demo.services.auth.AuthService;
import com.example.polling_api_demo.services.jwt.UserService;
import com.example.polling_api_demo.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest) {
        if(authService.hasMaxUsers()) {
            return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE)
                    .body(Collections.singletonMap("message", "Too many users are currently registered"));
        }

        try {
            if (authService.hasUserWithEmail(signupRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap("message", "User already exists"));
            }

            UserDTO createdUser = authService.createUser(signupRequest);
            if (createdUser == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("message", "User creation failed, please try again later"));
            }

            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(createdUser.getEmail());
            String jwt = jwtUtil.generateToken(userDetails, createdUser.getId());

            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setJwtToken(jwt);
            authenticationResponse.setName(createdUser.getFirstName() + " " + createdUser.getLastName());

            return ResponseEntity.status(HttpStatus.CREATED).body(authenticationResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );

            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(authenticationRequest.getEmail());
            Optional<User> optionalUser = userRepository.findFirstByEmail(userDetails.getUsername());

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String jwt = jwtUtil.generateToken(userDetails, user.getId());

                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setJwtToken(jwt);
                authenticationResponse.setName(user.getFirstName() + " " + user.getLastName());

                return ResponseEntity.ok(authenticationResponse);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "User not found"));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Incorrect username or password"));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("message", "User account is disabled"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetRequest resetRequest) {
        String email = resetRequest.getEmail();
        if (!authService.hasUserWithEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "User with email '" + email + "' does not exist"));
        }
        if (!authService.isResetPermitted(email)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                    .body(Collections.singletonMap("message", "You have already reset your password. Please try again later"));
        }
        try {
            UserDTO reset = authService.resetPassword(email);
            return ResponseEntity.ok(reset);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Password reset failed: " + e.getMessage()));
        }
    }
}
