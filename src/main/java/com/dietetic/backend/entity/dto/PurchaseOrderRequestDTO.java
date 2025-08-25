package com.dietetic.backend.entity.dto;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderRequestDTO {
    private Long userId;
    private List<PurchaseItemDTO> items;

    @Data
    public static class PurchaseItemDTO {
        private Long productId;
        private int quantity;
    }
}
