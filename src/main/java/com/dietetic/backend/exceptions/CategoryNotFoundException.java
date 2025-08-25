package com.dietetic.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Categoría con ID: " + id + " no encontrada.");
    }

    public CategoryNotFoundException(String name) {
        super("Categoría con nombre: '" + name + "' no encontrada.");
    }
}
