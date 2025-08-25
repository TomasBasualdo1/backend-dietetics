package com.dietetic.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dietetic.backend.entity.Product;
import com.dietetic.backend.entity.PurchaseItem;
import com.dietetic.backend.entity.PurchaseOrder;
import com.dietetic.backend.entity.PurchaseOrderStatus;
import com.dietetic.backend.entity.User;
import com.dietetic.backend.exceptions.PurchaseOrderInsufficientStockException;
import com.dietetic.backend.exceptions.PurchaseOrderInvalidStateException;
import com.dietetic.backend.exceptions.PurchaseOrderNotFoundException;
import com.dietetic.backend.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public Page<PurchaseOrder> getPagedPurchaseOrders(PageRequest pageRequest) {
        return purchaseOrderRepository.findAll(pageRequest);
    }

    @Override
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public PurchaseOrder getPurchaseOrderById(Long id) throws PurchaseOrderNotFoundException {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException(id));
    }

    @Override
    public List<PurchaseOrder> getPurchaseOrdersByUserId(Long userId) {
        return purchaseOrderRepository.findByUserId(userId);
    }

    @Override
    public List<PurchaseOrder> getPurchaseOrdersByStatus(PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public PurchaseOrder createPurchaseOrder(User user, List<PurchaseItem> items) {
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .status(PurchaseOrderStatus.PENDING)
                .user(user)
                .build();

        items.forEach(item ->
                purchaseOrder.addItem(item.getProduct(), item.getQuantity())
        );

        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public PurchaseOrder updatePurchaseOrder(Long id, User user, List<PurchaseItem> items) throws PurchaseOrderInvalidStateException {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(id);
        if (purchaseOrder.getStatus() != PurchaseOrderStatus.PENDING) {
            throw new PurchaseOrderInvalidStateException(id);
        }

        purchaseOrder.setUser(user);
        purchaseOrder.clearItems();
        items.forEach(item ->
                purchaseOrder.addItem(item.getProduct(), item.getQuantity())
        );
        purchaseOrder.preUpdate();

        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deletePurchaseOrderById(Long id) {
        PurchaseOrder purchaseOrder = getPurchaseOrderById(id);
        purchaseOrderRepository.delete(purchaseOrder);
        // TODO: We could add the same status validation here, maybe this whole endpoint isn't necessary
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void confirmPurchaseOrder(Long id) throws PurchaseOrderInvalidStateException, PurchaseOrderInsufficientStockException {
        PurchaseOrder order = getPurchaseOrderById(id);
        if (order.getStatus() != PurchaseOrderStatus.PENDING) {
            throw new PurchaseOrderInvalidStateException(id);
        }

        for (PurchaseItem item : order.getItems()) {
            Product product = item.getProduct();
            int quantity = item.getQuantity();

            if (product.getStock() < quantity) {
                throw new PurchaseOrderInsufficientStockException(product.getName(), product.getStock(), quantity);
            }

            product.setStock(product.getStock() - quantity);
        }

        order.setStatus(PurchaseOrderStatus.CONFIRMED);
        purchaseOrderRepository.save(order);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void cancelPurchaseOrder(Long id) {
        PurchaseOrder order = getPurchaseOrderById(id);

        if (order.getStatus() != PurchaseOrderStatus.PENDING) {
            throw new PurchaseOrderInvalidStateException(id);
        }

        order.setStatus(PurchaseOrderStatus.CANCELLED);
        purchaseOrderRepository.save(order);
    }

}
