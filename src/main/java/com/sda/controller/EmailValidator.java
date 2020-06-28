package com.sda.controller;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {
    @Override
    public void initialize(EmailConstraint emailConstraint){}

    @Override
    public boolean isValid(String email, ConstraintValidatorContext cxt){
        return email != null&&email.matches()
    }
}
