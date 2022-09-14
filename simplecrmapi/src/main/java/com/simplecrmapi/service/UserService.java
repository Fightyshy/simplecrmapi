package com.simplecrmapi.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.simplecrmapi.entity.User;

public interface UserService extends UserDetailsService{
	public User saveUser(User user);
	
	public List<User> findAllUsers();
	
	public User findUsername(String username);

	public User updateUserPassword(User user, String password);

	public User updateUserPassword(User user);
	
	public UserDetails loadUserByUsername(String username);
}
