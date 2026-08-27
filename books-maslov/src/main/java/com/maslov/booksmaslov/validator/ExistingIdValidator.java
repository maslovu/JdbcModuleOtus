package com.maslov.booksmaslov.validator;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ExistingIdValidator implements ConstraintValidator<ExistingId, Long> {

    private final EntityManager em;
    private Class<?> targetEntity; // Сюда сохраним класс из аннотации

    public ExistingIdValidator(EntityManager em) {
        this.em = em;
    }

    // Метод вызывается один раз при старте приложения для каждого помеченного поля
    @Override
    public void initialize(ExistingId annotation) {
        // Здесь имя параметра 'annotation'. Именно оно должно использоваться ниже.
        this.targetEntity = annotation.entityClass();
    }

    // Вызывается каждый раз при проверке данных
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        // Проверяем наличие записи в базе
        Object found = em.find(targetEntity, value);
        return found != null;
    }
}