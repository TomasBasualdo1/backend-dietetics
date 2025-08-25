package com.dietetic.backend.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dietetic.backend.entity.Product;
import com.dietetic.backend.service.ProductService;

@RestController
@RequestMapping("/api/bulk-upload")
public class BulkImageUploadController {

    @Autowired
    private ProductService productService;

    @PostMapping("/images")
    public ResponseEntity<?> uploadImagesFromDirectory() {
        Map<String, Object> results = new HashMap<>();
        Map<String, String> overriddenImages = new HashMap<>();
        
        try {
            // busca las imagenes en el directorio images
            ClassPathResource resource = new ClassPathResource("images");
            File directory = resource.getFile();
            
            if (!directory.exists() || !directory.isDirectory()) {
                return ResponseEntity.badRequest().body("Images directory not found in resources");
            }

            File[] files = directory.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".jpg") || 
                name.toLowerCase().endsWith(".jpeg") || 
                name.toLowerCase().endsWith(".png") ||
                name.toLowerCase().endsWith(".webp")
            );

            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest().body("No image files found in images directory");
            }

            for (File file : files) {
                try {
                    String fileName = file.getName();
                    // extrae el product id del nombre del archivo
                    String idStr = fileName.replaceAll("[^0-9]", "");
                    if (!idStr.isEmpty()) {
                        Long productId = Long.parseLong(idStr);
                        
                        // se fija si el prod ya tiene una imagen
                        Product product = productService.getProductById(productId);
                        if (product.getImageData() != null) {
                            overriddenImages.put(fileName, "Product " + productId + " already had an image");
                        }
                        
                        Path path = Paths.get(file.getAbsolutePath());
                        byte[] imageBytes = Files.readAllBytes(path);
                        
                        // crea archivo multi-part 
                        MultipartFile multipartFile = new MockMultipartFile(
                            fileName,
                            fileName,
                            Files.probeContentType(path),
                            imageBytes
                        );

                        productService.uploadProductImage(productId, multipartFile);
                        results.put(fileName, "Success");
                    }
                } catch (Exception e) {
                    results.put(file.getName(), "Error: " + e.getMessage());
                }
            }

            if (!overriddenImages.isEmpty()) {
                results.put("overridden_images", overriddenImages);
            }

            return ResponseEntity.ok(results);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error accessing images directory: " + e.getMessage());
        }
    }
}


class MockMultipartFile implements MultipartFile {
    private final byte[] content;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    public MockMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public java.io.InputStream getInputStream() throws IOException {
        return new java.io.ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        Files.write(dest.toPath(), content);
    }
} 