package com.dietetic.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PurchaseOrderInsufficientStockException extends RuntimeException {
    public PurchaseOrderInsufficientStockException(String productName, int currentStock, int requestedStock) {
        super("El stock del producto '" + productName + "' es de " + currentStock + " unidades, por lo que no es suficiente para cubrir la demanda de " + requestedStock + " unidades");
    }
}
