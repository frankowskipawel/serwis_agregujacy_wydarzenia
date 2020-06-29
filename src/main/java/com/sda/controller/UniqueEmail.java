package com.sda.controller;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface UniqueEmail {
    public String message() default "There is already a user with this email!";
    public Class<?>[] groups() default{};
    public Class<? extends Payload>[] payload() default{};
}
