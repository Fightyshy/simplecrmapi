package com.simplecrmapi.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplecrmapi.dao.UserDAO;
import com.simplecrmapi.entity.Role;
import com.simplecrmapi.entity.User;

import ch.qos.logback.classic.Logger;

@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	public UserDAO userDAO;
	
	@Autowired
	public RoleService roleService;
	
//	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder=passwordEncoder;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		User user = userDAO.findByUsername(username);
		if(user==null) {
			throw new UsernameNotFoundException("Invalid username or password");
		}
		//Because user implements userdetails, can directly return
//		user.setAuthorities(getAuthority(user));
		return user;
//		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
	}
	
	
    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }
    
    
	@Override
	@Transactional
	public User saveUser(User user) {
		User newUser = user;
		newUser.setId(0);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		Role role = roleService.findRoleByName("EMPLOYEE");
		Set<Role> roleSet = new HashSet<>();
		roleSet.add(role);
		
		newUser.setRoles(roleSet);
		return userDAO.save(newUser);
	}
	
	@Override
	@Transactional
	public User updateUserPassword(User user) {
		user.setEnabled(false);
		user.setPassword(null);
		return user;
	}
	
	@Override
	@Transactional
	public User updateUserPassword(User user, String password) {
		user.setPassword(passwordEncoder.encode(password));
		user.setEnabled(true);
		return userDAO.save(user);
	}

	@Override
	@Transactional
	public List<User> findAllUsers() {
		return userDAO.findAll();
	}

	@Override
	@Transactional
	public User findUsername(String username) {
		return userDAO.findByUsername(username);
	}

	
}
