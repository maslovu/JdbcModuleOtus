package com.maslov.booksmaslov.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NoBookException extends RuntimeException {
    public NoBookException(String message) {
        super(message);
    }
}
