package com.simplecrmapi.entity;

import javax.validation.constraints.Email;

public class ResetPW {
	
	private String username;
	
	@Email
	private String email;

	public ResetPW(String username, @Email String email) {
		this.username = username;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
