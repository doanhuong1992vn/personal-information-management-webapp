package com.user_service.exception;

import java.io.Serial;

public class DuplicateUsernameException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateUsernameException(String message) {
        super(message);
    }
}
