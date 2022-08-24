package com.simplecrmapi.rest;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simplecrmapi.entity.AuthToken;
import com.simplecrmapi.entity.Login;
import com.simplecrmapi.entity.PWResetToken;
import com.simplecrmapi.entity.ResetPW;
import com.simplecrmapi.entity.User;
import com.simplecrmapi.service.EmployeeService;
import com.simplecrmapi.service.UserService;
import com.simplecrmapi.util.TokenProvider;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private UserService userService;
    
    @Autowired
    private EmployeeService employeeService;
    
    @PostMapping("/authenticate")
    public ResponseEntity<Object> generateToken(@RequestBody Login login) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                		login.getUsername(),
                		login.getPassword()
                )
        );
        System.out.println("test");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        return ResponseEntity.ok(new AuthToken(token));
    }
    
    //Key Authorization, Value "Bearer <Token>"
    
    @PostMapping("/retrieve-user")
    public ResponseEntity<User> getUserDetails(@RequestBody Login login){
    	try {
    		final Authentication authentication = authenticationManager.authenticate(
    				new UsernamePasswordAuthenticationToken(
    						login.getUsername(),
    						login.getPassword())
    				);
    		
    		return ResponseEntity.ok((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    	}catch(Exception e){
    		return null;
    	}
    }
    
    @PostMapping("/register")
    public User saveUser(@RequestBody User user){
        return userService.saveUser(user);
    }
    
    @PostMapping("/resetpw")
    public ResponseEntity<Object> resetPasswordByEmailGetToken(@RequestBody @Valid ResetPW resetPW) throws AuthenticationException{
    	User user = userService.findUsername(resetPW.getUsername());
    	if(employeeService.getEmployeeByID(user.getEmployeeID()).getEmailAddress().equals(resetPW.getEmail())) {
    		String token = jwtTokenUtil.generatePWToken(user);
    		userService.updateUserPassword(user);
    		return ResponseEntity.ok(new AuthToken(token));
    	}else {
    		return ResponseEntity.notFound().build();
    	}
    }
    
    //workaround so submit both token and password at the same time
    @PostMapping("/changePW")
    public ResponseEntity<Object> submitPWChangeRequest(@RequestBody PWResetToken reset){
    	String username = jwtTokenUtil.getUsernameFromToken(reset.getToken());
    	User user = userService.findUsername(username);
    	if(user.isEnabled()==false) {
    		user = userService.updateUserPassword(user, reset.getPassword());
    	}else {
    		user = null;
    		return ResponseEntity.badRequest().build();
    	}
    	
    	return ResponseEntity.ok("Password successfully changed");
    }
    		
}
