package com.simplecrmapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.simplecrmapi.util.JwtAuthenticationFilter;
import com.simplecrmapi.util.UnauthorizedEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SimpleCRMSecurityConfig extends WebSecurityConfigurerAdapter {
//	@Resource(name="userService")
	@Autowired
	@Qualifier("userDetailsService")
	private UserDetailsService userDetailsService;
	
	@Autowired
	 private UnauthorizedEntryPoint unauthorizedEntryPoint;
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
//	@Override
//	@Autowired
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//	
////		auth.jdbcAuthentication().dataSource(securityDataSource);//		auth.authenticationProvider(authenticationProvider());
//		UserBuilder users = User.withDefaultPasswordEncoder();
//		
//		auth.inMemoryAuthentication()
//			.withUser(users.username("john").password("test123").roles("CUSTOMER"))
//			.withUser(users.username("mary").password("test123").roles("EMPLOYEE"))
//			.withUser(users.username("susan").password("test123").roles("ADMIN"));
//	}
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable() 
                .authorizeRequests()
                .antMatchers("/users/authenticate", "/users/register").permitAll()
                .antMatchers(HttpMethod.GET,"/customers/**", "/customers", "/customers/id", "/customers/id/**").hasAnyRole("EMPLOYEE","ADMIN","MANAGER")
                .antMatchers(HttpMethod.POST,"/customers/**", "/customers", "/customers/id", "/customers/id/**").hasAnyRole("EMPLOYEE","ADMIN","MANAGER")
                .antMatchers(HttpMethod.PUT,"/customers/**", "/customers", "/customers/id", "/customers/id/**").hasAnyRole("EMPLOYEE","ADMIN","MANAGER")
                .antMatchers(HttpMethod.DELETE,"/customers/**", "/customers", "/customers/id", "/customers/id/**").hasAnyRole("ADMIN","MANAGER")
                .antMatchers(HttpMethod.GET,"/employees/id/users").authenticated()
                .antMatchers(HttpMethod.GET,"/employees/id/**").hasAnyRole("MANAGER","ADMIN")
                .antMatchers(HttpMethod.GET,"/employees/id/users/**").hasAnyRole("EMPLOYEE","MANAGER","ADMIN")
                .antMatchers(HttpMethod.POST,"/employees/id","/employees").hasAnyRole("MANAGER","ADMIN")
                .antMatchers(HttpMethod.PUT,"/employees","employees/id").hasAnyRole("MANAGER","ADMIN")
                .antMatchers(HttpMethod.DELETE,"/employees","/employees/**").hasAnyRole("EMPLOYEE","ADMIN","MANAGER")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }
	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		//show x role certain pages, ** is everything after x roles, * is everything after char
//		//Fixed because you're not supposed to put in the root, dumbass
//		http.authorizeRequests()
//			.antMatchers("/", "/customers", "/employees", "/cases").permitAll()
//			.anyRequest().authenticated()
//			.and()
//		.formLogin()
//			.loginPage("/login")
//			.permitAll()
//			.and()
//		.logout()
//			.permitAll()
//		.and()
//		.httpBasic()
//		.and()
//		.csrf().disable()
//		.cors().disable()
//		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}
	
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }
}

