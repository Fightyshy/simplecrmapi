package com.simplecrmapi.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = EmailNotFoundValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailNotFound {
	public String message() default "Specified email is not found";
	
	public Class<?>[] groups() default{};
	
	public Class<? extends Payload>[] payload() default {};
}
