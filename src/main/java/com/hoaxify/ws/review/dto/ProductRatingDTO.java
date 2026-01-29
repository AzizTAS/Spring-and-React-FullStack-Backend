package com.hoaxify.ws.review.dto;

public class ProductRatingDTO {

    private long productId;
    private double averageRating;
    private long totalReviews;

    public ProductRatingDTO(long productId, double averageRating, long totalReviews) {
        this.productId = productId;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    // Getters and Setters
    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

}
