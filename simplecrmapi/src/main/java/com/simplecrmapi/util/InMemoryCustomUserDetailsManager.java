package com.simplecrmapi.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.simplecrmapi.entity.User;

public class InMemoryCustomUserDetailsManager extends InMemoryUserDetailsManager {
	
	private final Map<String, User> users = new HashMap<>();
	
	public InMemoryCustomUserDetailsManager(Collection<User> users) {
		for(User user:users) {
			createUser(user);
		}
	}
	
	public InMemoryCustomUserDetailsManager(User...users) {
		for(User user:users) {
			createUser(user);
		}
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.users.get(username.toLowerCase());
		
		if(user==null) {
			throw new UsernameNotFoundException(username);
		}
		
		return new User(user.getId(), user.getUsername(), user.getPassword(), user.getRoles(), true, user.getEmployee());
	}
	
	public void createUser(User user) {
		this.users.put(user.getUsername(), user);
	}
	
}
