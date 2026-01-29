package com.hoaxify.ws.review.exception;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(long id) {
        super("Review not found with id: " + id);
    }

    public ReviewNotFoundException(String message) {
        super(message);
    }

}
