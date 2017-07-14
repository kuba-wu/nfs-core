package com.kubawach.nfs.core.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kubawach.nfs.core.web.validate.JsrEnabledSystemValidator;

@ControllerAdvice
public class SimulationControllerAdvices {
    
    @Autowired private JsrEnabledSystemValidator systemValidator;
    
    @InitBinder("system")
    protected void initValidation(WebDataBinder binder) {
        binder.setValidator(systemValidator);
    }
    
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected String handleSystemNotSetException(SystemNotSetException exception) {
        
        return "Before running a simulation system you please submit a system first ('Submit' button).";
    }
    
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected String handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        
        List<ObjectError> errors = exception.getBindingResult().getAllErrors();
        StringBuilder response = new StringBuilder();
        response.append("System definition validation error.\n\n");
        response.append("Following errors were found:.\n");
        for (ObjectError error : errors) {
            response.append("- ");
            response.append(error.getDefaultMessage());
            response.append("\n");
        }
        return response.toString();
    }
}
