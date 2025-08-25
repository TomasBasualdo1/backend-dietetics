package com.dietetic.backend.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dietetic.backend.entity.Product;
import com.dietetic.backend.exceptions.CategoryNotFoundException;
import com.dietetic.backend.exceptions.ProductDuplicateException;
import com.dietetic.backend.exceptions.ProductNotFoundException;
import com.dietetic.backend.repository.CategoryRepository;
import com.dietetic.backend.repository.ProductRepository;

import jakarta.persistence.EntityManager;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Product> getPagedProducts(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) throws ProductNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product getProductByName(String name) throws ProductNotFoundException {
        return productRepository.findByName(name)
                .orElseThrow(() -> new ProductNotFoundException(name));
    }

    @Override
    public List<Product> searchProductsByName(String searchTerm) { 
        return productRepository.findByNameContainingIgnoreCase(searchTerm);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Product createProduct(String name, String description, BigDecimal price, Integer stock, Long categoryId, MultipartFile image, BigDecimal discountPercentage) throws ProductDuplicateException {
        if (productRepository.existsByName(name)) {
            throw new ProductDuplicateException(name);
        }
        if (!categoryRepository.existsById(categoryId)){
            throw new CategoryNotFoundException(categoryId);
        }

        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .categoryId(categoryId)
                .discountPercentage(discountPercentage)
                .build();

        if (image != null && !image.isEmpty()) {
            try {
                product.setImageData(new SerialBlob(image.getBytes()));
                product.setImageType(image.getContentType());
            } catch (IOException | SQLException e) {
                throw new RuntimeException("Error processing image", e);
            }
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Product updateProduct(Long id, String name, String description, BigDecimal price, Integer stock, Long categoryId, MultipartFile image, BigDecimal discountPercentage) throws ProductDuplicateException {
        Product product = getProductById(id);
        if (productRepository.existsByName(name) &&
                !product.getName().equals(name)) {
            throw new ProductDuplicateException(name);
        }
        if (!categoryRepository.existsById(categoryId)){
            throw new CategoryNotFoundException(categoryId);
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategoryId(categoryId);
        product.setDiscountPercentage(discountPercentage);

        if (image != null && !image.isEmpty()) {
            try {
                product.setImageData(new SerialBlob(image.getBytes()));
                product.setImageType(image.getContentType());
            } catch (IOException | SQLException e) {
                throw new RuntimeException("Error processing image", e);
            }
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteProductById(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Product uploadProductImage(Long productId, MultipartFile image) throws ProductNotFoundException {
        Product product = getProductById(productId);
        
        try {
            product.setImageData(new SerialBlob(image.getBytes()));
            product.setImageType(image.getContentType());
            return productRepository.save(product);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Error processing image", e);
        }
    }

    @Override
    public byte[] getProductImage(Long productId) throws ProductNotFoundException {
        Product product = getProductById(productId);
        
        if (product.getImageData() == null) {
            return null;
        }

        try {
            return product.getImageData().getBytes(1, (int) product.getImageData().length());
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving image", e);
        }
    }
}
