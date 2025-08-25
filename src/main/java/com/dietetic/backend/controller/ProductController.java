package com.dietetic.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dietetic.backend.entity.Product;
import com.dietetic.backend.entity.dto.ProductImageDTO;
import com.dietetic.backend.entity.dto.ProductRequestDTO;
import com.dietetic.backend.entity.dto.ProductResponseDTO;
import com.dietetic.backend.service.ProductService;

@RestController
@RequestMapping("products")
public class ProductController {
    @Autowired
    private ProductService productService;

    // GET /products
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponseDTO> productDTOs = products.stream()
                .map(ProductResponseDTO::fromProduct)
                .toList();

        return ResponseEntity.ok(productDTOs);
    }

    // GET /products/paged
    @GetMapping("/paged")
    public ResponseEntity<Page<ProductResponseDTO>> getPagedProducts(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        Page<Product> productPage = productService.getPagedProducts(PageRequest.of(page, size));
        Page<ProductResponseDTO> productDTOPage = productPage.map(ProductResponseDTO::fromProduct);

        return ResponseEntity.ok(productDTOPage);
    }

    // GET /products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ProductResponseDTO.fromProduct(product));
    }

    // GET /products/name/{name}
    @GetMapping("/name/{name}")
    public ResponseEntity<ProductResponseDTO> getProductByName(@PathVariable String name) {
        Product product = productService.getProductByName(name);
        return ResponseEntity.ok(ProductResponseDTO.fromProduct(product));
    }
    
    // GET /products/search/{searchTerm}
    @GetMapping("/search/{searchTerm}")
    public ResponseEntity<List<ProductResponseDTO>> searchProductsByName(@PathVariable String searchTerm) {
        List<Product> products = productService.searchProductsByName(searchTerm);
        List<ProductResponseDTO> productDTOs = products.stream()
                .map(ProductResponseDTO::fromProduct)
                .toList();
        return ResponseEntity.ok(productDTOs);
    }

    // GET /products/category/{categoryId}
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        List<ProductResponseDTO> productDTOs = products.stream()
                .map(ProductResponseDTO::fromProduct)
                .toList();

        return ResponseEntity.ok(productDTOs);
    }

    // POST /products
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(ProductRequestDTO request) {
        Product savedProduct = productService.createProduct(
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            request.getStock(),
            request.getCategoryId(),
            request.getImage(),
            request.getDiscountPercentage()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Producto " + savedProduct.getName() + " creado con éxito"));
    }

    // PUT /products/{id}
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(@PathVariable Long id, ProductRequestDTO request) {
        Product updatedProduct = productService.updateProduct(
            id,
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            request.getStock(),
            request.getCategoryId(),
            request.getImage(),
            request.getDiscountPercentage()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("message", "Producto " + updatedProduct.getName() + " actualizado con éxito"));
    }

    // POST /products/{id}/image
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProductImage(@PathVariable Long id, ProductImageDTO request) {
        Product updatedProduct = productService.uploadProductImage(id, request.getImage());
        return ResponseEntity.ok(Map.of("message", "Imagen del producto actualizada con éxito"));
    }

    // GET /products/{id}/image
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        byte[] imageData = productService.getProductImage(id);
        if (imageData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageData);
    }

    // DELETE /products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok(Map.of("message", "Producto eliminado con éxito"));
    }

}
