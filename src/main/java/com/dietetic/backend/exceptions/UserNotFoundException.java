package com.dietetic.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuario con ID: " + id + " no encontrado.");
    }

    public UserNotFoundException(String email) {
        super("Usuario con email: '" + email + "' no encontrado.");
    }

}
