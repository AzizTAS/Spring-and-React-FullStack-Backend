package com.hoaxify.ws.product;

import java.math.BigDecimal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hoaxify.ws.category.CategoryService;
import com.hoaxify.ws.product.dto.ProductUpdate;
import com.hoaxify.ws.product.exception.ProductNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public void save(Product product) {
        try {
            productRepository.saveAndFlush(product);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Product with name '" + product.getName() + "' already exists");
        }
    }

    public Page<Product> getProducts(Pageable page) {
        return productRepository.findAll(page);
    }

    public Product getProduct(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Page<Product> getProductsByCategory(long categoryId, Pageable page) {
        categoryService.getCategory(categoryId); // Validate category exists
        return productRepository.findByCategoryId(categoryId, page);
    }

    @Transactional
    public Product updateProduct(long id, ProductUpdate productUpdate) {
        Product product = getProduct(id);
        product.setName(productUpdate.getName());
        product.setDescription(productUpdate.getDescription());
        product.setPrice(productUpdate.getPrice());
        product.setStock(productUpdate.getStock());
        product.setImage(productUpdate.getImage());

        // Update category if categoryId changed
        if (productUpdate.getCategoryId() != null 
                && productUpdate.getCategoryId() != product.getCategory().getId()) {
            product.setCategory(categoryService.getCategory(productUpdate.getCategoryId()));
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(long id) {
        Product product = getProduct(id);
        productRepository.delete(product);
    }

    @Transactional
    public void decreaseStock(long productId, int quantity) {
        Product product = getProduct(productId);
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    public Page<Product> searchProducts(String keyword, Pageable page) {
        if (keyword == null || keyword.isBlank()) {
            return getProducts(page);
        }
        return productRepository.searchByKeyword(keyword, page);
    }

    public Page<Product> filterByPrice(BigDecimal minPrice, BigDecimal maxPrice, Pageable page) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, page);
    }

    public Page<Product> filterByCategoryAndPrice(long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
            Pageable page) {
        categoryService.getCategory(categoryId); // Validate category exists
        return productRepository.findByCategoryAndPriceRange(categoryId, minPrice, maxPrice, page);
    }

}
