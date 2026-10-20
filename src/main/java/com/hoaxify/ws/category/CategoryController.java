package com.hoaxify.ws.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.hoaxify.ws.category.dto.CategoryCreate;
import com.hoaxify.ws.shared.GenericMessage;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Page<Category> getCategories(Pageable page) {
        return categoryService.getCategories(page);
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Category createCategory(@Valid @RequestBody CategoryCreate dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return categoryService.save(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Category updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryCreate dto) {
        Category category = categoryService.getCategory(id);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return categoryService.save(category);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public GenericMessage deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return new GenericMessage("Category deleted");
    }
}
