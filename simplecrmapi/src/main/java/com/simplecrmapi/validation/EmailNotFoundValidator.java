package com.simplecrmapi.validation;

import java.util.Collections;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.simplecrmapi.entity.EmailWrapper;

public class EmailNotFoundValidator implements ConstraintValidator<EmailNotFound, String> {

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public void initialize(EmailNotFound constraintAnnotation) {

	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		HttpHeaders headers = new HttpHeaders();
		
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Location", "/loginpage");
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:8080/users/emailChecker").queryParam("email", value);
		
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<Object> checker = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Object.class);
		
		return (boolean) checker.getBody();
	}

}
