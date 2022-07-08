package com.simplecrmapi.service;

import java.util.List;

import com.simplecrmapi.entity.User;

public interface UserService {
	public User saveUser(User user);
	
	public List<User> findAllUsers();
	
	public User findUsername(String username);
}
