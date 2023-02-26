package com.hps.bookshop.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return true;
        }
        if (isEmptyName(multipartFile.getOriginalFilename())) {
            customMessageForValidation(constraintValidatorContext,"Name file is required");
            return false;
        }
        if (multipartFile.getContentType() == null || isNotSupportedContentType(multipartFile.getContentType())) {
            customMessageForValidation(constraintValidatorContext,"File only supports png,jpg,jpeg");
            return false;
        }
        if (isExceedSize(multipartFile.getSize())) {
            customMessageForValidation(constraintValidatorContext,"File cannot not exceed 5MB");
            return false;
        }
        return true;
    }

    private boolean isEmptyName(String originalFilename) {
        return originalFilename == null || originalFilename.isEmpty();
    }

    private boolean isNotSupportedContentType(String contentType) {
        return !(contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg")
        );
    }

    private boolean isExceedSize(long size) {
        return size > 5000000;
    }
    private void customMessageForValidation(ConstraintValidatorContext constraintContext, String message) {
        constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
