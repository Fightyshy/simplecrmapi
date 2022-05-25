package com.simplecrmapi.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class CustomerInvalidSocialMediaException extends NullPointerException {
	public CustomerInvalidSocialMediaException() {
		super();
	}
}
