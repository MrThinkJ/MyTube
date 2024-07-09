package com.mrthinkj.videoservice.validator;

import com.mrthinkj.videoservice.annotation.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    @Override
    public void initialize(ValidImage constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = true;
        if (file == null)
            return false;
        if (!isSupportedContentType(file.getContentType())){
            result = false;
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                            "Only image file types are allowed.")
                    .addConstraintViolation();
        }
        return result;
    }

    private boolean isSupportedContentType(String contentType){
        return contentType!= null && (contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg"));
    }
}
