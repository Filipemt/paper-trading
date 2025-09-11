package com.filipecode.papertrading.infrastructure.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CPFValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CPF {
    String message() default "CPF Inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
