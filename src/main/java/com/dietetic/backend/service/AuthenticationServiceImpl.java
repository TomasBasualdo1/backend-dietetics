package com.dietetic.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.dietetic.backend.config.JwtService;
import com.dietetic.backend.entity.dto.AuthenticationResponseDTO;
import com.dietetic.backend.entity.User;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponseDTO getToken(User user) {
        return AuthenticationResponseDTO.builder()
                .accessToken(jwtService.generateToken(user))
                .build();
    }

    public void authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );
    }

}
