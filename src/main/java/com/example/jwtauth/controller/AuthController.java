package com.example.jwtauth.controller;


import com.example.jwtauth.config.security.JwtTokenProvider;
import com.example.jwtauth.exception.AppException;
import com.example.jwtauth.model.Role;
import com.example.jwtauth.model.RoleType;
import com.example.jwtauth.model.User;
import com.example.jwtauth.payload.*;
import com.example.jwtauth.service.IUserService;
import com.example.jwtauth.service.Impl.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    IUserService userService;

    @Autowired
    RoleService roleService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;


    @PostMapping ( "/signin" )
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        String refreshJwt = tokenProvider.createRefreshToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, refreshJwt));
    }

    @PostMapping ( "/signup" )
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (userService.existsByUserName(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        Role userRole = roleService.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getPassword(), signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(userRole));
        User result = userService.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUserName()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    @PostMapping ( value = "/refresh", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<?> refreshToken(@RequestBody RefreshToken refreshToken) {
        try {
            return ResponseEntity.ok().body(tokenProvider.refreshAccessToken(refreshToken.getRefreshToken()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("couldn't create the refresh token");
        }
    }

}