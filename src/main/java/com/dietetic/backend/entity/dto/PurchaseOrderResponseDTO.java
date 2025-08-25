package com.dietetic.backend.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.dietetic.backend.entity.Product;
import com.dietetic.backend.entity.PurchaseOrder;
import com.dietetic.backend.entity.User;

import lombok.Data;

@Data
public class PurchaseOrderResponseDTO {
    private Long id;
    private UserSummaryDTO user;
    private List<ItemDetailDTO> items;
    private BigDecimal subtotal;
    private LocalDateTime orderDate;
    private String status;

    @Data
    public static class UserSummaryDTO {
        private Long id;
        private String email;
        private String address;
        private String firstName;
        private String lastName;
    }

    @Data
    public static class ItemDetailDTO {
        private Long productId;
        private String productName;
        private BigDecimal unitPrice;
        private int quantity;
    }

    public static PurchaseOrderResponseDTO fromPurchaseOrder(PurchaseOrder order) {
        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getDate());
        dto.setSubtotal(order.getSubtotal());
        dto.setStatus(order.getStatus().name());

        User user = order.getUser();
        UserSummaryDTO userDto = new UserSummaryDTO();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setAddress(user.getAddress());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        dto.setUser(userDto);


    List<ItemDetailDTO> items = order.getItems().stream().map(item -> {
        ItemDetailDTO itemDto = new ItemDetailDTO();
        Product product = item.getProduct(); // Obtener el producto
        itemDto.setProductId(product.getId());
        itemDto.setProductName(product.getName());

        BigDecimal effectivePrice = product.getPrice();
        BigDecimal discount = product.getDiscountPercentage();
        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discount.divide(new BigDecimal("100")));
            effectivePrice = effectivePrice.multiply(discountMultiplier);
        }
        itemDto.setUnitPrice(effectivePrice); // Usar precio con descuento
        itemDto.setQuantity(item.getQuantity());
        return itemDto;
    }).toList();


        dto.setItems(items);
        return dto;
    }
}
