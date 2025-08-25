package com.dietetic.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDuplicateException extends RuntimeException {
    public UserDuplicateException(String email) {
        super("El email: '" + email + "' ya está en uso por otro usuario.");
    }
}
