package com.simplecrmapi.util;

import javax.persistence.EntityNotFoundException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(EntityNotFound.class)
	protected ResponseEntity<Object> handleEntityNotFound(EntityNotFound e){
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("404 NOT FOUND: Entity not found due to invalid ID");
		
	}
	
	//overriding
	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException e){
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("404 NOT FOUND: Entity not found due to invalid ID");
		
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<Object> handleInvalidParams(MethodArgumentTypeMismatchException e){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("400 BAD REQUEST: Invalid params");
	}
	
	@ExceptionHandler(CustomerInvalidAddressException.class)
	protected ResponseEntity<Object> handleInvalidAddress(CustomerInvalidAddressException e){
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("406 NOT ACCEPTABLE: Customer is missing non-null/empty address object(s)");
	}
	
	@ExceptionHandler(CustomerInvalidSocialMediaException.class)
	protected ResponseEntity<Object> handleInvalidSocialMedia(CustomerInvalidSocialMediaException e){
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("406 NOT ACCEPTABLE: Customer is missing non-null social media object");
	}
	
	@ExceptionHandler(CustomerInvalidObjectsException.class)
	protected ResponseEntity<Object> handleInvalidSocialMedia(CustomerInvalidObjectsException e){
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("406 NOT ACCEPTABLE: Customer is missing non-null/empty social media and address object(s)");
	}
}
