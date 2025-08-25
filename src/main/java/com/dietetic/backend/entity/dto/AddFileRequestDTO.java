package com.dietetic.backend.entity.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class AddFileRequestDTO {
    private String name;
    private MultipartFile file;
} 