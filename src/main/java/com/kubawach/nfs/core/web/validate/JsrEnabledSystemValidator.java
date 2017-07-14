package com.kubawach.nfs.core.web.validate;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.kubawach.nfs.core.model.system.System;

@Component
public class JsrEnabledSystemValidator implements Validator {

    @Autowired private javax.validation.Validator jsrValidator;
    @Autowired private SystemValidator systemValidator;
    
    @Override
    public boolean supports(Class<?> clazz) {

        return (System.class.equals(clazz));
    }

    @Override
    public void validate(Object target, Errors errors) {

        System system = (System)target;
        
        Set<ConstraintViolation<System>> violations = jsrValidator.validate(system, Default.class);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<System> violation : violations) {
                errors.reject("", "Property '"+violation.getPropertyPath()+"' "+violation.getMessage());
            }
            return ;
        }
        // run system validator
        systemValidator.validate(target, errors);
    }

}
