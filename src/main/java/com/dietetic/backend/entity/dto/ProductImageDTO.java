package com.dietetic.backend.entity.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ProductImageDTO {
    private Long productId;
    private MultipartFile image;
} 