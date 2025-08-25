package com.dietetic.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductDuplicateException extends RuntimeException {
    public ProductDuplicateException(String name) {
        super("El producto con el nombre '" + name + "' ya existe.");
    }
}
