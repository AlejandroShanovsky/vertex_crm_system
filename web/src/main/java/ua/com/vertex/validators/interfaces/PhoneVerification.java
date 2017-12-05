package ua.com.vertex.validators.interfaces;

import ua.com.vertex.validators.PhoneVerificationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PhoneVerificationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneVerification {
    String message() default "ua.com.vertex.validators.interfaces.PhoneVerification.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
