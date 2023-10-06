package com.user_service.exception;

import org.springframework.validation.FieldError;

import java.io.Serial;
import java.util.List;

public class CustomValidationException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<FieldError> errors;

    public CustomValidationException(String message, List<FieldError> errors) {
        super(message);
        this.errors = errors;
    }

    public List<FieldError> getErrors() {
        return errors;
    }
}
