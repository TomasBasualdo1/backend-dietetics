package com.dietetic.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PurchaseItemInvalidException extends RuntimeException {
    public PurchaseItemInvalidException() {
        super("Producto o cantidad no válidos.");
    }
}
