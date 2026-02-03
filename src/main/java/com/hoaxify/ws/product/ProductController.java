package com.hoaxify.ws.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import com.hoaxify.ws.product.dto.ProductCreate;
import com.hoaxify.ws.product.dto.ProductDTO;
import com.hoaxify.ws.shared.GenericMessage;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductDTO> getProducts(Pageable page) {
        return productService.getProducts(page).map(ProductDTO::new);
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable Long id) {
        return new ProductDTO(productService.getProduct(id));
    }

    @GetMapping("/category/{categoryId}")
    public Page<ProductDTO> getProductsByCategory(@PathVariable Long categoryId, Pageable page) {
        return productService.getProductsByCategory(categoryId, page).map(ProductDTO::new);
    }

    @GetMapping("/search")
    public Page<ProductDTO> searchProducts(@RequestParam(required = false) String keyword, Pageable page) {
        return productService.searchProducts(keyword, page).map(ProductDTO::new);
    }

    @PostMapping
    public ProductDTO createProduct(@RequestBody ProductCreate dto) {
        return new ProductDTO(productService.save(dto));
    }

    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @RequestBody ProductCreate dto) {
        return new ProductDTO(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    public GenericMessage deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return new GenericMessage("Product deleted");
    }
}
