package com.hoaxify.ws.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({DisabledException.class, AccessDeniedException.class})
    ResponseEntity<?> handleDisabledException(Exception exception, HttpServletRequest request){
        ApiError error = new ApiError();
        error.setMessage(exception.getMessage());
        error.setPath(request.getRequestURI());
        if(exception instanceof DisabledException) {
            error.setStatus(401);
        } else if (exception instanceof AccessDeniedException){
            error.setStatus(403);
        }
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> handleAllExceptions(Exception exception, HttpServletRequest request) {
        ApiError error = new ApiError();
        error.setPath(request.getRequestURI());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        
        error.setMessage(exception.getClass().getName() + ": " + exception.getMessage() + " --- " + stackTrace.substring(0, Math.min(500, stackTrace.length())));
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}