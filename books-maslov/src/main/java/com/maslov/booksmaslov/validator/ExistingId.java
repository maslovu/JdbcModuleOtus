package com.maslov.booksmaslov.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingIdValidator.class)
public @interface ExistingId {

    Class<?> entityClass(); // Сущность, которую нужно проверить

    String message() default "Объект с таким ID не найден";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}