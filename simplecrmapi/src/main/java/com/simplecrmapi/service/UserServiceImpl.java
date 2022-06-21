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

import com.simplecrmapi.dao.UserDAO;
import com.simplecrmapi.entity.User;
import com.simplecrmapi.entity.Role;

@Service(value="userService")
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	public UserDAO userDAO;
	
	@Autowired
	public RoleService roleService;
	
//	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		User user = userDAO.findByUsername(username);
		if(user==null) {
			throw new UsernameNotFoundException("Invalid username or password");
		}
		//Because user implements userdetails, can directly return
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
	public User saveUser(User user) {
		User newUser = user;
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		Role role = roleService.findRoleByName("EMPLOYEE");
		Set<Role> roleSet = new HashSet<>();
		roleSet.add(role);
		
		newUser.setRoles(roleSet);
		return userDAO.save(newUser);
	}

	@Override
	public List<User> findAllUsers() {
		return userDAO.findAll();
	}

	@Override
	public User fineUsername(String username) {
		return userDAO.findByUsername(username);
	}

}
