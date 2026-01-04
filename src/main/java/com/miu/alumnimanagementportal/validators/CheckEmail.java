package com.miu.alumnimanagementportal.validators;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.apache.commons.validator.routines.EmailValidator;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CheckEmail.ValidatorValue.class)
public @interface CheckEmail {

    int value() default 0;

    String message() default "invalid email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@link CheckEmail} annotations on the same element
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        CheckEmail[] value();
    }

    class ValidatorValue implements ConstraintValidator<CheckEmail, String> {

        @Override
        public void initialize(CheckEmail constraintAnnotation) {
        }

        @Override
        public boolean isValid(String content, ConstraintValidatorContext constraintValidatorContext) {
            if (content == null) {
                return false;
            }
            return EmailValidator.getInstance().isValid(content);
        }
    }
}
