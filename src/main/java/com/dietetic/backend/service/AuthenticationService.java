package com.dietetic.backend.service;

import com.dietetic.backend.entity.User;
import com.dietetic.backend.entity.dto.AuthenticationResponseDTO;

public interface AuthenticationService {

    AuthenticationResponseDTO getToken(User user);

    void authenticate(String email, String password);

}
