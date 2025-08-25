package com.dietetic.backend.entity;

import java.math.BigDecimal;
import java.sql.Blob;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    @Positive(message = "El precio debe ser mayor que 0")
    private BigDecimal price;

    @Column(nullable = false)
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @JoinColumn(name = "category_id", nullable = false)
    private Long categoryId;

    @Lob
    @Column(name = "image_data")
    private Blob imageData;

    @Column(name = "image_type")
    private String imageType;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    @PositiveOrZero(message = "El porcentaje de descuento no puede ser negativo")
    private BigDecimal discountPercentage;
}