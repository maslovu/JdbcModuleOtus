package com.maslov.booksmaslov.exception;

public class NoBookException extends RuntimeException{
    public NoBookException(String message) {
        super(message);
    }
}
