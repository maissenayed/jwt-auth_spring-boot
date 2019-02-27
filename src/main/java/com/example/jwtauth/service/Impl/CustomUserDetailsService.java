package com.example.jwtauth.service.Impl;



import com.example.jwtauth.config.security.JwtTokenProvider;
import com.example.jwtauth.config.security.UserPrincipal;
import com.example.jwtauth.model.User;
import com.example.jwtauth.repository.RoleRepo;
import com.example.jwtauth.repository.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepo userRepository;
    @Autowired
    RoleRepo roleRepository;

    // This method is used by JWTAuthenticationFilter
    @Autowired
    JwtTokenProvider tokenProvider;

    @Transactional
    public UserDetails loadUserById(Long id) {
        com.example.jwtauth.model.User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );
        System.out.println(user.toString());

        return UserPrincipal.create(user);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {
        User user = userRepository.findByUserNameOrEmail(usernameOrEmail, usernameOrEmail);
        return UserPrincipal.create(user);
    }

}