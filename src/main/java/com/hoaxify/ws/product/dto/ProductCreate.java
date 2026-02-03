package com.hoaxify.ws.product.dto;

import java.math.BigDecimal;

import com.hoaxify.ws.product.Product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductCreate {

    @NotBlank(message = "{hoaxify.constraints.product.name.NotBlank.message}")
    @Size(min = 3, max = 255, message = "{hoaxify.constraints.product.name.Size.message}")
    private String name;

    @Size(max = 1000, message = "{hoaxify.constraints.product.description.Size.message}")
    private String description;

    @NotNull(message = "{hoaxify.constraints.product.price.NotNull.message}")
    @DecimalMin(value = "0.01", message = "{hoaxify.constraints.product.price.DecimalMin.message}")
    private BigDecimal price;

    @NotNull(message = "{hoaxify.constraints.product.stock.NotNull.message}")
    @Min(value = 0, message = "{hoaxify.constraints.product.stock.Min.message}")
    private Integer stock;

    @NotNull(message = "{hoaxify.constraints.product.categoryId.NotNull.message}")
    private Long categoryId;

    private String image;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Product toProduct() {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImage(image);
        return product;
    }

}
