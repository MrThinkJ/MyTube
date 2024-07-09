package com.mrthinkj.videoservice.annotation;

import com.mrthinkj.videoservice.validator.VideoFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {VideoFileValidator.class})
public @interface ValidVideo {
    String message() default "Invalid video file";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
