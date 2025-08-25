package com.dietetic.backend.service;

import org.springframework.stereotype.Service;

import com.dietetic.backend.entity.Image;

@Service
public interface ImageService {
    Image create(Image image);
    Image viewById(Long id);
} 
