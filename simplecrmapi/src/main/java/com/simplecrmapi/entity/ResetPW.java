package com.simplecrmapi.entity;

import javax.validation.constraints.Email;

public class ResetPW {
	
	private String token;
	
	private String password;
	
	private String confirmPassword;

	public ResetPW(String token, String password, String confirmPassword) {
		this.token = token;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
