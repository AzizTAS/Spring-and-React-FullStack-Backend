package com.hoaxify.ws.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoaxify.ws.configuration.CurrentUser;
import com.hoaxify.ws.review.dto.ReviewCreate;
import com.hoaxify.ws.review.dto.ReviewDTO;
import com.hoaxify.ws.review.dto.ReviewUpdate;
import com.hoaxify.ws.shared.GenericMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/product/{productId}")
    Page<ReviewDTO> getProductReviews(@PathVariable long productId, Pageable page) {
        return reviewService.getProductReviews(productId, page).map(ReviewDTO::new);
    }

    @GetMapping("/{id}")
    ReviewDTO getReview(@PathVariable long id) {
        return new ReviewDTO(reviewService.getReview(id));
    }

    @GetMapping("/product/{productId}/rating")
    Double getProductAverageRating(@PathVariable long productId) {
        return reviewService.getProductAverageRating(productId);
    }

    @PostMapping("/product/{productId}")
    @PreAuthorize("authenticated")
    ReviewDTO createReview(@PathVariable long productId, @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody ReviewCreate reviewCreate) {
        return new ReviewDTO(reviewService.createReview(productId, currentUser, reviewCreate));
    }

    @PutMapping("/{id}")
    @PreAuthorize("authenticated")
    ReviewDTO updateReview(@PathVariable long id, @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody ReviewUpdate reviewUpdate) {
        return new ReviewDTO(reviewService.updateReview(id, currentUser, reviewUpdate));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("authenticated")
    GenericMessage deleteReview(@PathVariable long id, @AuthenticationPrincipal CurrentUser currentUser) {
        reviewService.deleteReview(id, currentUser);
        return new GenericMessage("Review deleted successfully");
    }

}
