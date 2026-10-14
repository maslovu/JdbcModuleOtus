package com.maslov.booksmaslov.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Аннотация говорит Spring: "Если выбросишь это, верни браузеру код 404"
@ResponseStatus(code = HttpStatus.NOT_FOUND)
@Getter
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Object fieldValue) {
        super(String.format("%s not found with id : %s", resourceName, fieldValue));
    }
}