package com.simplecrmapi.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidParamsException extends IllegalArgumentException {
	public InvalidParamsException() {
		super();
	}
}