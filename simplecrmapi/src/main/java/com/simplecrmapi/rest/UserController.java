package com.simplecrmapi.rest;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simplecrmapi.entity.EmailWrapper;
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

    private AuthenticationManager authenticationManager;
    private TokenProvider jwtTokenUtil;
    private UserService userService;
    private EmployeeService employeeService;
    private final Logger log;
    
	public UserController(AuthenticationManager authenticationManager, TokenProvider jwtTokenUtil,
			UserService userService, EmployeeService employeeService, Logger log) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
		this.userService = userService;
		this.employeeService = employeeService;
		this.log = log;
	}
	
    @PostMapping("/authenticate")
    public ResponseEntity<Object> generateToken(@RequestBody Login login) throws AuthenticationException {
    	try {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                		login.getUsername(),
                		login.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        return ResponseEntity.ok(token);
    	}catch(BadCredentialsException be){
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    	}
    }
    
    //Key Authorization, Value "Bearer <Token>"
    
    @PostMapping("/retrieve-user")
    public ResponseEntity<Object> getUserDetails(@RequestBody Login login){
    	
    	UserDetails userDetails = userService.loadUserByUsername(login.getUsername());
    	
    	return ResponseEntity.ok(userDetails);
    	
//    	User user = (User) userService.loadUserByUsername(username);
//    	final Authentication authentication = authenticationManager.authenticate(
//    			new UsernamePasswordAuthenticationToken(
//    					user.getUsername(),
//    					user.getPassword())
//    			);
//    	SecurityContextHolder.getContext().setAuthentication(authentication);
//    	return authentication;
    	
//    	return (User) userService.loadUserByUsername(username); 	
//    	try {
//    	System.out.println("test2");
//    		final Authentication authentication = authenticationManager.authenticate(
//    				new UsernamePasswordAuthenticationToken(
//    						login.getUsername(),
//    						login.getPassword())
//    				);
//    		SecurityContextHolder.getContext().setAuthentication(authentication);
//    		System.out.println("test");
////    		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
////    		return ResponseEntity.ok((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//    		return ResponseEntity.ok(authentication);
//    	}catch(Exception e){
//    		System.out.println("test2");
//    		return ResponseEntity.notFound().build();
//    	}
    }
    
    @PostMapping("/register")
    public User saveUser(@RequestBody User user){
        return userService.saveUser(user);
    }
    
    @PostMapping("/issue-pw-token")
    public ResponseEntity<Object> IssuePasswordResetToken(@RequestBody @Valid EmailWrapper email){
    	User user = userService.findByEmailAddress(email.getEmail());
    	if(user==null||user.equals(null)) {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    	}
    	userService.updateUserPassword(user);
    	final String pwToken = jwtTokenUtil.generatePWToken(email.getEmail());
    	System.out.println(pwToken);
    	return ResponseEntity.ok(pwToken);
	}
    
    @PostMapping("/resetpw")
    public ResponseEntity<Object> UseTokenToResetPassword(@RequestBody ResetPW resetPW) throws AuthenticationException{
    	
    	String email = jwtTokenUtil.getUsernameFromToken(resetPW.getToken());
    	int employeeID = employeeService.getEmployeeByEmailAddress(email).getId();
    	User user = userService.findUserByEmployeeID(employeeID);
    	if(!jwtTokenUtil.validateToken(resetPW.getToken(), user)) {
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    	}
    	String passwordChecker = resetPW.getPassword().equalsIgnoreCase(resetPW.getConfirmPassword())?resetPW.getPassword():"";

    	if(user.isEnabled()==false && !passwordChecker.equals("")) {
    		user.setPassword(passwordChecker);
    		user = userService.updateUserPassword(user, passwordChecker);
    	}else {
    		user = null;
    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("And error has occured");
    	}
    	log.info(user.getUsername()+" has successfully recovered and reset their password");
    	return ResponseEntity.ok().build();
    }
    
//    @Deprecated //wonky password reset, switching to using jwt tokens
//    @PostMapping("/resetpw")
//    public ResponseEntity<Object> resetPasswordByEmailGetToken(@RequestBody @Valid ResetPW resetPW) throws AuthenticationException{
//    	int employeeID = employeeService.getEmployeeByEmailAddress(resetPW.getEmail()).getId();
//    	User user = userService.findUserByEmployeeID(employeeID);
//    	if(user==null||user.equals(null)) {
//    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    	}
//    	
//    	System.out.println(resetPW.getPassword());
//    	System.out.println(resetPW.getConfirmPassword());
//    	String passwordChecker = resetPW.getPassword().equalsIgnoreCase(resetPW.getConfirmPassword())?resetPW.getPassword():"";
//    	System.out.println("PWChecker: "+passwordChecker);
//    	if(!passwordChecker.equals("")) {
////    		user.setPassword(passwordChecker);
//    		userService.updateUserPassword(user, passwordChecker);
//    	}else {
//    		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    	}
//    	
//    	return ResponseEntity.ok().build();
//    	User user = userService.findUsername(resetPW.getUsername());
//    	if(employeeService.getEmployeeByID(user.getEmployeeID()).getEmailAddress().equals(resetPW.getEmail())) {
//    		String token = jwtTokenUtil.generatePWToken(user);
//    		userService.updateUserPassword(user);
//    		return ResponseEntity.ok(new AuthToken(token));
//    	}else {
//    		return ResponseEntity.notFound().build();
//    	}
//    }
    
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
    
    @GetMapping("/emailChecker")
    public ResponseEntity<Object> checkEmaiLAddress(@RequestParam("email") String email){
    	return userService.findByEmailAddress(email)!=null?ResponseEntity.ok(true):ResponseEntity.notFound().build();
    }
    		
}
