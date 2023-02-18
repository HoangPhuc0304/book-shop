package com.hps.bookshop.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {
    private String firstField;
    private String secondField;

    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        firstField = constraintAnnotation.first();
        secondField = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        Object fistObj = new BeanWrapperImpl(o).getPropertyValue(firstField);
        Object secondObj = new BeanWrapperImpl(o).getPropertyValue(secondField);
        if (fistObj != null) {
            return fistObj.equals(secondObj);
        } else {
            return secondObj == null;
        }
    }
}
