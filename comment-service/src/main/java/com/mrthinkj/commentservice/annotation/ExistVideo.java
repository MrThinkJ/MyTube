package com.mrthinkj.commentservice.annotation;

import com.mrthinkj.commentservice.validator.ExistVideoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExistVideoValidator.class})
public @interface ExistVideo {
    String message() default "This video id is not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
