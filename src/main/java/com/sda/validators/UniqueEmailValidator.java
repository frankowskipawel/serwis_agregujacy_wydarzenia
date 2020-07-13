package com.sda.validators;

import com.sda.entity.User;
import com.sda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    @Autowired
    UserService userService;

    @Override
    public void initialize(UniqueEmail constraintAnnotation){}

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        User foundUser = userService.findUsersByEmail(email);
        if (foundUser != null) {
            return false;
        } else {
            return true;
        }
    }
}