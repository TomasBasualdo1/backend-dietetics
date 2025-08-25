package com.dietetic.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PurchaseOrderNotFoundException extends RuntimeException {

    public PurchaseOrderNotFoundException(Long id) {
        super("Orden de compra con ID: " + id + " no encontrada.");
    }

}
