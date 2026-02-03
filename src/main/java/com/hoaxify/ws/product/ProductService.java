package com.hoaxify.ws.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hoaxify.ws.category.CategoryService;
import com.hoaxify.ws.product.dto.ProductCreate;
import com.hoaxify.ws.product.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Product save(ProductCreate dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImage(dto.getImage());
        if (dto.getCategoryId() != null) {
            product.setCategory(categoryService.getCategory(dto.getCategoryId()));
        }
        return productRepository.save(product);
    }

    public Page<Product> getProducts(Pageable page) {
        return productRepository.findAll(page);
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable page) {
        return productRepository.findByCategoryId(categoryId, page);
    }

    public Page<Product> searchProducts(String keyword, Pageable page) {
        if (keyword == null || keyword.isBlank()) {
            return getProducts(page);
        }
        return productRepository.searchByKeyword(keyword, page);
    }

    public Product updateProduct(Long id, ProductCreate dto) {
        Product product = getProduct(id);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImage(dto.getImage());
        if (dto.getCategoryId() != null) {
            product.setCategory(categoryService.getCategory(dto.getCategoryId()));
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void decreaseStock(Long productId, int quantity) {
        Product product = getProduct(productId);
        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        product.setStock(newStock);
        productRepository.save(product);
    }

    public void increaseStock(Long productId, int quantity) {
        Product product = getProduct(productId);
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }
}
