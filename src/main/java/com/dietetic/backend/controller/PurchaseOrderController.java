package com.dietetic.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dietetic.backend.entity.Product;
import com.dietetic.backend.entity.PurchaseItem;
import com.dietetic.backend.entity.PurchaseOrder;
import com.dietetic.backend.entity.PurchaseOrderStatus;
import com.dietetic.backend.entity.User;
import com.dietetic.backend.entity.dto.PurchaseOrderRequestDTO;
import com.dietetic.backend.entity.dto.PurchaseOrderResponseDTO;
import com.dietetic.backend.service.ProductService;
import com.dietetic.backend.service.PurchaseOrderService;
import com.dietetic.backend.service.UserService;

@RestController
@RequestMapping("purchase-orders")
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // GET /purchase-orders
    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getAllOrders() {
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.getAllPurchaseOrders();
        List<PurchaseOrderResponseDTO> purchaseOrderDTOs = purchaseOrders.stream()
                .map(PurchaseOrderResponseDTO::fromPurchaseOrder)
                .toList();

        return ResponseEntity.ok(purchaseOrderDTOs);
    }

    // GET /purchase-orders/paged
    @GetMapping("/paged")
    public ResponseEntity<Page<PurchaseOrderResponseDTO>> getPagedCategories(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        Page<PurchaseOrder> purchaseOrderPage = purchaseOrderService.getPagedPurchaseOrders(PageRequest.of(page, size));
        Page<PurchaseOrderResponseDTO> purchaseOrderDTOPage = purchaseOrderPage.map(PurchaseOrderResponseDTO::fromPurchaseOrder);

        return ResponseEntity.ok(purchaseOrderDTOPage);
    }

    // GET /purchase-orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponseDTO> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
        return ResponseEntity.ok(PurchaseOrderResponseDTO.fromPurchaseOrder(purchaseOrder));
    }

    // GET /purchase-orders/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getPurchaseOrdersByUser(@PathVariable Long userId) {
        List<PurchaseOrderResponseDTO> orders = purchaseOrderService.getPurchaseOrdersByUserId(userId)
                .stream().map(PurchaseOrderResponseDTO::fromPurchaseOrder).toList();
        return ResponseEntity.ok(orders);
    }

    // GET /purchase-orders/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrderResponseDTO>> getPurchaseOrdersByStatus(@PathVariable PurchaseOrderStatus status) {
        List<PurchaseOrderResponseDTO> orders = purchaseOrderService.getPurchaseOrdersByStatus(status)
                .stream().map(PurchaseOrderResponseDTO::fromPurchaseOrder).toList();
        return ResponseEntity.ok(orders);
    }

    // POST /purchase-orders
    @PostMapping
    public ResponseEntity<?> createPurchaseOrder(@RequestBody PurchaseOrderRequestDTO requestDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            System.out.println("Creating purchase order for user: " + userEmail);
            
            User user = userService.getUserByEmail(userEmail);
            System.out.println("Found user: " + user.getId());

            List<PurchaseItem> items = requestDTO.getItems().stream()
                    .map(purchaseItemDTO -> {
                        Product product = productService.getProductById(purchaseItemDTO.getProductId());
                        System.out.println("Processing item: " + product.getName() + " x " + purchaseItemDTO.getQuantity());
                        return PurchaseItem.builder()
                                .product(product)
                                .quantity(purchaseItemDTO.getQuantity())
                                .build();
                    })
                    .toList();

            PurchaseOrder savedPurchaseOrder = purchaseOrderService.createPurchaseOrder(user, items);
            System.out.println("Created purchase order with ID: " + savedPurchaseOrder.getId());
            
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("message", "Orden de compra número: " + savedPurchaseOrder.getId().toString() + " creada con éxito"));
        } catch (Exception e) {
            System.err.println("Error creating purchase order: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // PUT /purchase-orders/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @PathVariable Long id,
            @RequestBody PurchaseOrderRequestDTO requestDTO
    ) {
        PurchaseOrder currentPurchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
        User user = requestDTO.getUserId() != null ? userService.getUserById(requestDTO.getUserId()) : currentPurchaseOrder.getUser();
        List<PurchaseItem> items = requestDTO.getItems() != null ? requestDTO.getItems().stream()
                .map(purchaseItemDTO -> PurchaseItem.builder()
                        .product(productService.getProductById(purchaseItemDTO.getProductId()))
                        .quantity(purchaseItemDTO.getQuantity())
                        .build())
                .toList() : currentPurchaseOrder.getItems();

        PurchaseOrder updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(id, user, items);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "Orden de compra número: " + id + " actualizada con éxito"));
    }

    // DELETE /purchase-orders/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrderById(id);
        return ResponseEntity.ok(Map.of("message", "Orden de compra número: " + id + " eliminada con éxito"));
    }

    // PUT /purchase-orders/{id}/confirm
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmPurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.confirmPurchaseOrder(id);
        return ResponseEntity.ok(Map.of("message", "Orden de compra número: " + id + " confirmada con éxito"));
    }

    // PUT /purchase-orders/{id}/cancel
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelPurchaseOrder(@PathVariable Long id) {
        purchaseOrderService.cancelPurchaseOrder(id);
        return ResponseEntity.ok(Map.of("message", "Orden de compra número: " + id + "  cancelada con éxito"));
    }

}
