package com.user_service.exception;

import jakarta.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LogManager.getLogger();


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllException(Exception ex) {
        logger.error(ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails("An exception occurred during request processing", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.error(ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails("Invalid input data. Validation failed", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error(ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails("Invalid input data", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<?> handleDuplicateFieldUserException(DuplicateUsernameException ex) {
        logger.error(ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails("User data is duplicated", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
