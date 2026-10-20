package com.hoaxify.ws.error;

import java.util.stream.Collectors;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hoaxify.ws.auth.exception.AuthenticationException;
import com.hoaxify.ws.cart.exception.CartNotFoundException;
import com.hoaxify.ws.category.exception.CategoryNotFoundException;
import com.hoaxify.ws.order.exception.OrderNotFoundException;
import com.hoaxify.ws.payment.exception.PaymentNotFoundException;
import com.hoaxify.ws.review.exception.ReviewNotFoundException;
import com.hoaxify.ws.shared.Messages;
import com.hoaxify.ws.user.exception.ActivationNotificationException;
import com.hoaxify.ws.user.exception.InvalidTokenException;
import com.hoaxify.ws.user.exception.NotFoundException;
import com.hoaxify.ws.user.exception.NotUniqueEmailException;
import com.hoaxify.ws.product.exception.ProductNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        NotUniqueEmailException.class,
        ActivationNotificationException.class,
        InvalidTokenException.class,
        NotFoundException.class,
        AuthenticationException.class,
        ProductNotFoundException.class,
        HttpMessageNotReadableException.class,
    })
    ResponseEntity<ApiError> handleException(Exception exception, HttpServletRequest request){
        ApiError apiError = new ApiError();
        apiError.setPath(request.getRequestURI());
        apiError.setMessage(exception.getMessage());
        if(exception instanceof MethodArgumentNotValidException) {
            String message = Messages.getMessageForLocale("hoaxify.error.validation", LocaleContextHolder.getLocale());
            apiError.setMessage(message);
            apiError.setStatus(400);
            var validationErrors = ((MethodArgumentNotValidException)exception).getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (existing, replacing) -> existing));
            apiError.setValidationErrors(validationErrors);
        } else if (exception instanceof NotUniqueEmailException) {
            apiError.setStatus(400);
            apiError.setValidationErrors(((NotUniqueEmailException)exception).getValidationErrors());
        } else if (exception instanceof ActivationNotificationException) {
            apiError.setStatus(502);
        } else if (exception instanceof InvalidTokenException) {
            apiError.setStatus(400);
        } else if (exception instanceof NotFoundException) {
            apiError.setStatus(404);
        } else if (exception instanceof ProductNotFoundException) {
            apiError.setStatus(404);
        } else if (exception instanceof AuthenticationException) {
            apiError.setStatus(401);
        } else if (exception instanceof HttpMessageNotReadableException) {
            apiError.setStatus(400);
            apiError.setMessage("Invalid request body: " + exception.getMessage());
        }

        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    @ExceptionHandler({
        CartNotFoundException.class,
        CategoryNotFoundException.class,
        OrderNotFoundException.class,
        PaymentNotFoundException.class,
        ReviewNotFoundException.class,
    })
    ResponseEntity<ApiError> handleNotFound(RuntimeException exception, HttpServletRequest request) {
        return build(404, exception, request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException exception, HttpServletRequest request) {
        ApiError apiError = new ApiError();
        apiError.setPath(request.getRequestURI());
        apiError.setMessage(Messages.getMessageForLocale("hoaxify.error.conflict", LocaleContextHolder.getLocale()));
        apiError.setStatus(409);
        return ResponseEntity.status(409).body(apiError);
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ApiError> handleBusinessRule(IllegalStateException exception, HttpServletRequest request) {
        return build(400, exception, request);
    }

    private ResponseEntity<ApiError> build(int status, Exception exception, HttpServletRequest request) {
        ApiError apiError = new ApiError();
        apiError.setPath(request.getRequestURI());
        apiError.setMessage(exception.getMessage());
        apiError.setStatus(status);
        return ResponseEntity.status(status).body(apiError);
    }
}