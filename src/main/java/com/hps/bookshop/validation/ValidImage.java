package com.hps.bookshop.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Constraint(validatedBy = ImageFileValidator.class)
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidImage {
    java.lang.String message() default "Invalid image file";

    java.lang.Class<?>[] groups() default {};

    java.lang.Class<? extends jakarta.validation.Payload>[] payload() default {};
}
