package com.hoaxify.ws.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewCreate {

    @NotNull(message = "{hoaxify.constraints.rating.NotNull.message}")
    @Min(value = 1, message = "{hoaxify.constraints.rating.Min.message}")
    @Max(value = 5, message = "{hoaxify.constraints.rating.Max.message}")
    private Integer rating;

    @Size(max = 1000, message = "{hoaxify.constraints.review.comment.Size.message}")
    private String comment;

    // Getters and Setters
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
