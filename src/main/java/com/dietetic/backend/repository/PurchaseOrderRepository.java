package com.dietetic.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dietetic.backend.entity.PurchaseOrder;
import com.dietetic.backend.entity.PurchaseOrderStatus;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder,Long>{
    List<PurchaseOrder> findByUserId(Long userId);

    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

}
