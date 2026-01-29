package com.hoaxify.ws.category;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoaxify.ws.category.dto.CategoryCreate;
import com.hoaxify.ws.category.dto.CategoryDTO;
import com.hoaxify.ws.category.dto.CategoryUpdate;
import com.hoaxify.ws.shared.GenericMessage;
import com.hoaxify.ws.shared.Messages;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    GenericMessage createCategory(@Valid @RequestBody CategoryCreate categoryCreate) {
        categoryService.save(categoryCreate.toCategory());
        String message = Messages.getMessageForLocale("hoaxify.create.category.success.message",
                LocaleContextHolder.getLocale());
        return new GenericMessage(message);
    }

    @GetMapping
    Page<CategoryDTO> getCategories(Pageable page) {
        return categoryService.getCategories(page).map(CategoryDTO::new);
    }

    @GetMapping("/{id}")
    CategoryDTO getCategory(@PathVariable long id) {
        return new CategoryDTO(categoryService.getCategory(id));
    }

    @PutMapping("/{id}")
    CategoryDTO updateCategory(@PathVariable long id, @Valid @RequestBody CategoryUpdate categoryUpdate) {
        return new CategoryDTO(categoryService.updateCategory(id, categoryUpdate));
    }

    @DeleteMapping("/{id}")
    GenericMessage deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        String message = Messages.getMessageForLocale("hoaxify.delete.category.success.message",
                LocaleContextHolder.getLocale());
        return new GenericMessage(message);
    }

}
