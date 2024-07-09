package com.mrthinkj.videoservice.validator;

import com.mrthinkj.videoservice.annotation.ValidVideo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class VideoFileValidator implements ConstraintValidator<ValidVideo, MultipartFile> {

    @Override
    public void initialize(ValidVideo constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        boolean result = true;
        String contentType = file.getContentType();
        if (!isSupportedContentType(contentType)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                            "Only video file types are allowed.")
                    .addConstraintViolation();
            result = false;
        }
        return result;
    }

    private boolean isSupportedContentType(String contentType){
        return contentType != null && contentType.startsWith("video/");
    }
}
