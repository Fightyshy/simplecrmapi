package com.simplecrmapi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NumbersInStringOnlyConstraintValidator implements ConstraintValidator<NumbersInStringOnly, String>{

	@Override
	public void initialize(NumbersInStringOnly numbersInStringOnly) {
		
	}
	
	@Override
	public boolean isValid(String fieldInput, ConstraintValidatorContext context) {
		return fieldInput==null||fieldInput.isEmpty()||fieldInput.matches("[0-9 ]+");
	}
	
}
