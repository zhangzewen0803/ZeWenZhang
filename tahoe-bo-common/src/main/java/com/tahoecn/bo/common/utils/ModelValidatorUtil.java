package com.tahoecn.bo.common.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ModelValidatorUtil {
    public static <T> List<String> modelValidator(T t){
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        List<String> messageList = new ArrayList<>();
        for(ConstraintViolation<?> constraintViolation : constraintViolations) {
            messageList.add(constraintViolation.getMessage());
        }

        return messageList;
    }
}
