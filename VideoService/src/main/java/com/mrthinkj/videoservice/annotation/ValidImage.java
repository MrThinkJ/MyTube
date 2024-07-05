package com.mrthinkj.videoservice.annotation;

import com.mrthinkj.videoservice.validator.ImageFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageFileValidator.class})
public @interface ValidImage {
    String message() default "Invalid image file";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
