package com.dietetic.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Producto con ID: " + id + " no encontrado.");
    }

    public ProductNotFoundException(String name) {
        super("Producto con nombre: '" + name + "' no encontrado.");
    }
}
