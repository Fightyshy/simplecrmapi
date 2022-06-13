package com.simplecrmapi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AlphanumericConstraintValidator implements ConstraintValidator<Alphanumeric, String>{

	@Override
	public void initialize(Alphanumeric alphanumeric) {
		
	}
	
	@Override
	public boolean isValid(String fieldInput, ConstraintValidatorContext context) {
		return fieldInput==null||fieldInput.length()==0||fieldInput.matches("[a-zA-Z0-9 ]+");
	}
	
}
