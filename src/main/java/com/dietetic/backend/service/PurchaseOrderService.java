package com.dietetic.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.dietetic.backend.entity.PurchaseItem;
import com.dietetic.backend.entity.PurchaseOrder;
import com.dietetic.backend.entity.PurchaseOrderStatus;
import com.dietetic.backend.entity.User;
import com.dietetic.backend.exceptions.PurchaseOrderInsufficientStockException;
import com.dietetic.backend.exceptions.PurchaseOrderInvalidStateException;
import com.dietetic.backend.exceptions.PurchaseOrderNotFoundException;

public interface PurchaseOrderService {
    Page<PurchaseOrder> getPagedPurchaseOrders(PageRequest pageRequest);

    List<PurchaseOrder> getAllPurchaseOrders();

    PurchaseOrder getPurchaseOrderById(Long id) throws PurchaseOrderNotFoundException;

    List<PurchaseOrder> getPurchaseOrdersByUserId(Long userId);

    List<PurchaseOrder> getPurchaseOrdersByStatus(PurchaseOrderStatus status);

    PurchaseOrder createPurchaseOrder(User user, List<PurchaseItem> items);

    PurchaseOrder updatePurchaseOrder(Long id, User user, List<PurchaseItem> items) throws PurchaseOrderInvalidStateException;

    void deletePurchaseOrderById(Long id);

    void confirmPurchaseOrder(Long id) throws PurchaseOrderNotFoundException, PurchaseOrderInsufficientStockException;

    void cancelPurchaseOrder(Long id) throws PurchaseOrderNotFoundException;

}
