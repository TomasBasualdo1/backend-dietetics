package com.dietetic.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import com.dietetic.backend.entity.Product;
import com.dietetic.backend.exceptions.ProductDuplicateException;
import com.dietetic.backend.exceptions.ProductNotFoundException;



public interface ProductService {
    List<Product> getAllProducts();

    Page<Product> getPagedProducts(PageRequest pageRequest);

    Product getProductById(Long id) throws ProductNotFoundException;

    Product getProductByName(String name) throws ProductNotFoundException;

    List<Product> searchProductsByName(String searchTerm);

    List<Product> getProductsByCategory(Long categoryId);

    Product createProduct(String name, String description, BigDecimal price, Integer stock, Long categoryId, MultipartFile image, BigDecimal discountPercentage) throws ProductDuplicateException; 

    Product updateProduct(Long id, String name, String description, BigDecimal price, Integer stock, Long categoryId, MultipartFile image, BigDecimal discountPercentage) throws ProductDuplicateException; 

    void deleteProductById(Long id);

    Product uploadProductImage(Long productId, MultipartFile image) throws ProductNotFoundException;

    byte[] getProductImage(Long productId) throws ProductNotFoundException;
}
