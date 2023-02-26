package com.hps.bookshop.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<PhoneConstraint, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isEmpty())
            return true;
        Pattern pattern = Pattern.compile("^([0-9]){9,11}");
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }


}
