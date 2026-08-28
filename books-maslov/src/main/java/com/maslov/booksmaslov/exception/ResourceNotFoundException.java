package com.maslov.booksmaslov.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Аннотация говорит Spring: "Если выбросишь это, верни браузеру код 404"
@ResponseStatus(code = HttpStatus.NOT_FOUND)
@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, Object fieldValue) {
        super(String.format("%s not found with id : '%s'", resourceName, fieldValue));
        this.resourceName = resourceName;
        this.fieldValue = fieldValue;
    }
}