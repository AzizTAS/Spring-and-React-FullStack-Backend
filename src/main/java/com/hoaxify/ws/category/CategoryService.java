package com.hoaxify.ws.category;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hoaxify.ws.category.dto.CategoryUpdate;
import com.hoaxify.ws.category.exception.CategoryNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void save(Category category) {
        try {
            categoryRepository.saveAndFlush(category);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Category with name '" + category.getName() + "' already exists");
        }
    }

    public Page<Category> getCategories(Pageable page) {
        return categoryRepository.findAll(page);
    }

    public Category getCategory(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Transactional
    public Category updateCategory(long id, CategoryUpdate categoryUpdate) {
        Category category = getCategory(id);
        category.setName(categoryUpdate.getName());
        category.setDescription(categoryUpdate.getDescription());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(long id) {
        Category category = getCategory(id);
        categoryRepository.delete(category);
    }

}
