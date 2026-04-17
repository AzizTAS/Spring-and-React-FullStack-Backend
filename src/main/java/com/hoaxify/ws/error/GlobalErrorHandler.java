package com.hoaxify.ws.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler({DisabledException.class, AccessDeniedException.class})
    ResponseEntity<?> handleDisabledException(Exception exception, HttpServletRequest request) {
        ApiError error = new ApiError();
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());
        if (exception instanceof DisabledException) {
            error.setStatus(HttpStatus.UNAUTHORIZED.value());
        } else {
            error.setStatus(HttpStatus.FORBIDDEN.value());
        }
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handleAllExceptions(Exception exception, HttpServletRequest request) {
        logger.error("Unhandled exception at {}: {}", request.getRequestURI(), exception.getMessage(), exception);
        ApiError error = new ApiError();
        error.setPath(request.getRequestURI());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage("An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
