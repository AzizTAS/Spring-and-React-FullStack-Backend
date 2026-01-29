package com.hoaxify.ws.review;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.product.Product;
import com.hoaxify.ws.product.ProductService;
import com.hoaxify.ws.review.dto.ReviewCreate;
import com.hoaxify.ws.review.dto.ReviewUpdate;
import com.hoaxify.ws.review.exception.ReviewNotFoundException;
import com.hoaxify.ws.user.User;
import com.hoaxify.ws.user.UserService;

import jakarta.transaction.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final UserService userService;

    public ReviewService(ReviewRepository reviewRepository, ProductService productService,
            UserService userService) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
        this.userService = userService;
    }

    public Page<Review> getProductReviews(long productId, Pageable page) {
        productService.getProduct(productId); // Validate product exists
        return reviewRepository.findByProductId(productId, page);
    }

    public Review getReview(long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Transactional
    public Review createReview(long productId, CurrentUser currentUser, ReviewCreate reviewCreate) {
        Product product = productService.getProduct(productId);
        User user = userService.getUser(currentUser.getId());

        // Check if user already reviewed this product
        Review existingReview = reviewRepository.findByProductIdAndUserId(productId, currentUser.getId());
        if (existingReview != null) {
            throw new RuntimeException("You have already reviewed this product");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(reviewCreate.getRating());
        review.setComment(reviewCreate.getComment());

        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(long id, CurrentUser currentUser, ReviewUpdate reviewUpdate) {
        Review review = getReview(id);

        // Check if user owns this review
        if (review.getUser().getId() != currentUser.getId()) {
            throw new RuntimeException("Unauthorized");
        }

        review.setRating(reviewUpdate.getRating());
        review.setComment(reviewUpdate.getComment());
        review.setUpdatedDate(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(long id, CurrentUser currentUser) {
        Review review = getReview(id);

        // Check if user owns this review
        if (review.getUser().getId() != currentUser.getId()) {
            throw new RuntimeException("Unauthorized");
        }

        reviewRepository.delete(review);
    }

    public Double getProductAverageRating(long productId) {
        productService.getProduct(productId); // Validate product exists
        return reviewRepository.getAverageRatingForProduct(productId);
    }

}
