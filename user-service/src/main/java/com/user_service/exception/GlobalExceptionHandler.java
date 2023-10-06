package com.user_service.exception;

import com.user_service.utils.MessageSrc;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LogManager.getLogger();

    private final MessageSrc messageSrc;


    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<CommonError> handleCustomViolationException(CustomValidationException ex) {
        logger.error(ex.getMessage());
        List<ErrorDetails> errorDetails = ex.getErrors()
                .parallelStream()
                .map(error -> new ErrorDetails(error.getField(), error.getRejectedValue(), error.getDefaultMessage()))
                .toList();
        CommonError commonError = new CommonError(ex.getMessage(), errorDetails);
        return new ResponseEntity<>(commonError, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonError> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.error(ex.getMessage());
        List<ErrorDetails> errorDetails = ex.getConstraintViolations()
                .parallelStream()
                .map(error -> new ErrorDetails(
                        error.getPropertyPath().toString(), error.getInvalidValue(), error.getMessage()))
                .toList();
        CommonError commonError = new CommonError(ex.getMessage(), errorDetails);
        return new ResponseEntity<>(commonError, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<CommonError> handleDuplicateFieldUserException(DuplicateUsernameException ex) {
        logger.error(ex.getMessage());
        CommonError commonError = new CommonError(
                messageSrc.getMessage("Error.user.register.duplication"), ex.getMessage()
        );
        return new ResponseEntity<>(commonError, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CommonError> handleBadCredentialsException(BadCredentialsException ex) {
        logger.error(ex.getMessage());
        CommonError commonError = new CommonError(messageSrc.getMessage("Error.user.login"), ex.getMessage());
        return new ResponseEntity<>(commonError, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<CommonError> handleInvalidTokenException(InvalidTokenException ex) {
        logger.error(ex.getMessage());
        CommonError commonError = new CommonError(messageSrc.getMessage("Error.invalid.token"), ex.getMessage());
        return new ResponseEntity<>(commonError, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonError> handleAuthenticationException(AuthenticationException ex) {
        logger.error(ex.getMessage());
        CommonError commonError = new CommonError(messageSrc.getMessage("Error.authentication"), ex.getMessage());
        return new ResponseEntity<>(commonError, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonError> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error(ex.getMessage());
        CommonError commonError = new CommonError(messageSrc.getMessage("Error.argument"), ex.getMessage());
        return new ResponseEntity<>(commonError, HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonError> handleAllException(Exception ex) {
        logger.error(ex.getMessage());
        CommonError commonError = new CommonError(messageSrc.getMessage("Error.exception"), ex.getMessage());
        return new ResponseEntity<>(commonError, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
