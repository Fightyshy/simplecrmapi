package com.simplecrmapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SimpleCRMSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	
//		auth.jdbcAuthentication().dataSource(securityDataSource);//		auth.authenticationProvider(authenticationProvider());
		UserBuilder users = User.withDefaultPasswordEncoder();
		
		auth.inMemoryAuthentication()
			.withUser(users.username("john").password("test123").roles("CUSTOMER"))
			.withUser(users.username("mary").password("test123").roles("EMPLOYEE"))
			.withUser(users.username("susan").password("test123").roles("ADMIN"));
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//show x role certain pages, ** is everything after x roles, * is everything after char
		//Fixed because you're not supposed to put in the root, dumbass
		http.authorizeRequests()
			.antMatchers("/", "/customers", "/employees", "/cases").permitAll()
			.anyRequest().authenticated()
			.and()
		.formLogin()
			.loginPage("/login")
			.permitAll()
			.and()
		.logout()
			.permitAll()
		.and()
		.httpBasic()
		.and()
		.csrf().disable()
		.cors().disable()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
}

