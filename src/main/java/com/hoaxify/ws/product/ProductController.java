package com.hoaxify.ws.product;

import java.math.BigDecimal;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hoaxify.ws.category.CategoryService;
import com.hoaxify.ws.product.dto.ProductCreate;
import com.hoaxify.ws.product.dto.ProductDTO;
import com.hoaxify.ws.product.dto.ProductUpdate;
import com.hoaxify.ws.shared.GenericMessage;
import com.hoaxify.ws.shared.Messages;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @PostMapping
    GenericMessage createProduct(@Valid @RequestBody ProductCreate productCreate) {
        Product product = productCreate.toProduct();
        product.setCategory(categoryService.getCategory(productCreate.getCategoryId()));
        productService.save(product);
        String message = Messages.getMessageForLocale("hoaxify.create.product.success.message",
                LocaleContextHolder.getLocale());
        return new GenericMessage(message);
    }

    @GetMapping
    Page<ProductDTO> getProducts(Pageable page) {
        return productService.getProducts(page).map(ProductDTO::new);
    }

    @GetMapping("/{id}")
    ProductDTO getProduct(@PathVariable long id) {
        return new ProductDTO(productService.getProduct(id));
    }

    @GetMapping("/category/{categoryId}")
    Page<ProductDTO> getProductsByCategory(@PathVariable long categoryId, Pageable page) {
        return productService.getProductsByCategory(categoryId, page).map(ProductDTO::new);
    }

    @GetMapping("/search")
    Page<ProductDTO> searchProducts(@RequestParam(required = false) String keyword, Pageable page) {
        return productService.searchProducts(keyword, page).map(ProductDTO::new);
    }

    @GetMapping("/filter/price")
    Page<ProductDTO> filterByPrice(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice,
            Pageable page) {
        return productService.filterByPrice(minPrice, maxPrice, page).map(ProductDTO::new);
    }

    @GetMapping("/filter/category/{categoryId}/price")
    Page<ProductDTO> filterByCategoryAndPrice(@PathVariable long categoryId,
            @RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice, Pageable page) {
        return productService.filterByCategoryAndPrice(categoryId, minPrice, maxPrice, page)
                .map(ProductDTO::new);
    }

    @PutMapping("/{id}")
    ProductDTO updateProduct(@PathVariable long id, @Valid @RequestBody ProductUpdate productUpdate) {
        return new ProductDTO(productService.updateProduct(id, productUpdate));
    }

    @DeleteMapping("/{id}")
    GenericMessage deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        String message = Messages.getMessageForLocale("hoaxify.delete.product.success.message",
                LocaleContextHolder.getLocale());
        return new GenericMessage(message);
    }

}
