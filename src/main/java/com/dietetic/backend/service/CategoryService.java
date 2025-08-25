package com.dietetic.backend.service;

import java.util.List;

import com.dietetic.backend.exceptions.CategoryDuplicateException;
import com.dietetic.backend.exceptions.CategoryNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import com.dietetic.backend.entity.Category;

public interface CategoryService {
    Page<Category> getPagedCategories(PageRequest pageRequest);

    List<Category> getAllCategories();

    Category getCategoryById(Long id) throws CategoryNotFoundException;

    Category getCategoryByName(String name) throws CategoryNotFoundException;

    Category createCategory(String name, String description) throws CategoryDuplicateException;

    Category updateCategory(Long id, String name, String description) throws CategoryDuplicateException;

    void deleteCategoryById(Long id);

}
