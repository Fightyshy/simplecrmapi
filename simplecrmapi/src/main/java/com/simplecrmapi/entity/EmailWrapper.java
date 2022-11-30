package com.simplecrmapi.entity;

import javax.validation.constraints.Email;

import com.simplecrmapi.validation.EmailNotFound;

public class EmailWrapper {
	@Email
	@EmailNotFound
	private String email;

	public EmailWrapper(@Email String email) {
		this.email = email;
	}

	public EmailWrapper() {

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
