package com.simplecrmapi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityNotFoundException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
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
	
	@ExceptionHandler(InvalidParamsException.class)
	protected ResponseEntity<Object> handleInvalidParams(InvalidParamsException e){
		return ResponseEntity.badRequest().body("400 BAD REQUEST: Invalid params");
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	protected ResponseEntity<Object> handleNoSuchElementOptional(NoSuchElementException e){
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("404 NOT FOUND: Failed to retrieve specified entity with given parameters from database");
	}
	
//	@ExceptionHandler(MethodArgumentNotValidException.class)
	//https://stackoverflow.com/questions/51991992/getting-ambiguous-exceptionhandler-method-mapped-for-methodargumentnotvalidexce
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request){
		List<FieldError> errors = ex.getBindingResult().getFieldErrors();
		List<String> trunc = new ArrayList<String>();
		for(FieldError err:errors) {
			trunc.add("Input error in field "+err.getField()+" with value "+err.getRejectedValue()+". "+err.getDefaultMessage());
		}
		return ResponseEntity.badRequest().body("400 BAD REQUEST: Validation failed due to: "+trunc.toString());
	}
}
