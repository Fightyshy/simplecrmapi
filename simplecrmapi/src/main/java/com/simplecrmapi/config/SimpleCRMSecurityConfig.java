package com.simplecrmapi.config;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.simplecrmapi.entity.Role;
import com.simplecrmapi.entity.User;
import com.simplecrmapi.util.InMemoryCustomUserDetailsManager;
import com.simplecrmapi.util.JwtAuthenticationFilter;
import com.simplecrmapi.util.UnauthorizedEntryPoint;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SimpleCRMSecurityConfig extends WebSecurityConfigurerAdapter {
	@Resource(name="userService")
//	@Autowired
//	@Qualifier("userService")
	private UserDetailsService userDetailsService;
	
	@Autowired
	 private UnauthorizedEntryPoint unauthorizedEntryPoint;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable() 
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users/issue-pw-token", "/users/reset-pw", "/users/emailChecker").permitAll()
                .antMatchers(HttpMethod.PUT, "/users/authenticate", "/users/register", "/users/retrieve-user").permitAll()
                .antMatchers(HttpMethod.GET, "/products/id").permitAll()
                .antMatchers(HttpMethod.GET,"/customers/**", "/customers", "/customers/id", "/customers/id/**").hasAnyRole("EMPLOYEE","ADMIN","MANAGER")
                .antMatchers(HttpMethod.POST,"/customers/**", "/customers", "/customers/id", "/customers/id/**").hasAnyRole("EMPLOYEE","ADMIN","MANAGER")
                .antMatchers(HttpMethod.PUT,"/customers/**", "/customers", "/customers/id", "/customers/id/**").hasAnyRole("EMPLOYEE","ADMIN","MANAGER")
                .antMatchers(HttpMethod.DELETE,"/customers/**", "/customers", "/customers/id", "/customers/id/**").hasAnyRole("ADMIN","MANAGER")
                .antMatchers(HttpMethod.GET, "/employees/users/**", "/employees/users", "/employees/id/**", "/employees/id", "/employees").authenticated()
                .antMatchers(HttpMethod.POST, "/employees/users/**", "/employees/user").authenticated()
                .antMatchers(HttpMethod.POST, "employees/id", "/employees/id/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers(HttpMethod.POST,"/employees").hasAnyRole("MANAGER","ADMIN")
                .antMatchers(HttpMethod.PUT, "/employees/user", "/emloyees/users/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/employees/id", "employees/id/**").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers(HttpMethod.PUT, "/employees").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers(HttpMethod.DELETE, "/employees/users/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/employees/id", "/employees/id/**", "/employees").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers(HttpMethod.GET, "/cases/**", "/cases", "/cases/id", "/cases/id/**").authenticated()
                .antMatchers(HttpMethod.POST, "/cases/users", "/cases/users/**", "/cases/users/*").authenticated()
                .antMatchers(HttpMethod.POST, "/cases/id", "/cases/id/**").hasAnyRole("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/cases/id", "/cases/id/**").hasAnyRole("MANAGER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/cases/users", "/cases/users/**", "/cases/users/*").authenticated()
                .antMatchers(HttpMethod.DELETE, "/cases", "/cases/**", "/cases/id", "/cases/id/**", "/cases/products/**").hasAnyRole("MANAGER", "ADMIN")
//                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("http://localhost:8081/logindefault")
                .invalidateHttpSession(true)
                .deleteCookies("token")
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().formLogin().disable();

        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }
	
//	@Bean
//	public BCryptPasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder(10);
//	}
	
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }
    
//    @Bean
//    public UserDetailsService testEmployeeDetails() {
//		Role emper = new Role("EMPLOYEE");
//		HashSet<Role> emloyee = new HashSet<>();
//		emloyee.add(emper);
//		User emp1 = new User(1, "employee1", "$2a$10$/lsJHIECOXOL9T8GAa5SGuskWo4E5dg/7neiYnYfqEeWqTV33dnZq",1 ,emloyee);
//		emp1.setEmployeeID(1);
//
//		//Another smoking gun https://babarowski.com/blog/mock-authentication-with-custom-userdetails/#final-solution
//		//But instead extended the class and created a (sloppy) custom implementation
//		//Which is basically the important parts using custom user
//		//TODO Move to separate testing config
//		return new InMemoryCustomUserDetailsManager(Arrays.asList(emp1));
//    }
}

