package com.hoaxify.ws.review.dto;

import java.time.LocalDateTime;

import com.hoaxify.ws.review.Review;

public class ReviewDTO {

    private long id;
    private long productId;
    private long userId;
    private String userName;
    private int rating;
    private String comment;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public ReviewDTO() {
    }

    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.productId = review.getProduct().getId();
        this.userId = review.getUser().getId();
        this.userName = review.getUser().getUsername();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdDate = review.getCreatedDate();
        this.updatedDate = review.getUpdatedDate();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

}
