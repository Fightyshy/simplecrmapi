package com.simplecrmapi.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AlphabetOnlyConstraintValidator implements ConstraintValidator<AlphabetOnly, String>{

	@Override
	public void initialize(AlphabetOnly alphabetOnly) {
		
	}
	
	@Override
	public boolean isValid(String fieldInput, ConstraintValidatorContext context) {
		return fieldInput==null||fieldInput.isEmpty()||fieldInput.matches("[a-zA-Z ]+");
	}
	
}
