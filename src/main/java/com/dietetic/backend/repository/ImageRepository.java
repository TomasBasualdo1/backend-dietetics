package com.dietetic.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dietetic.backend.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
} 
