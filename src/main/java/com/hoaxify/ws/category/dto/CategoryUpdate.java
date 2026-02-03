package com.hoaxify.ws.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryUpdate {

    @NotBlank(message = "{hoaxify.constraints.category.name.NotBlank.message}")
    @Size(min = 3, max = 255, message = "{hoaxify.constraints.category.name.Size.message}")
    private String name;

    @Size(max = 1000, message = "{hoaxify.constraints.category.description.Size.message}")
    private String description;

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

}
