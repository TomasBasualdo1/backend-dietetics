package com.dietetic.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PurchaseOrderInvalidStateException extends RuntimeException {
    public PurchaseOrderInvalidStateException(Long id) {
        super("La orden de compra número: " + id + " no se encuentra en estado pendiente, por lo que ya no se puede modificar.");
    }
}
