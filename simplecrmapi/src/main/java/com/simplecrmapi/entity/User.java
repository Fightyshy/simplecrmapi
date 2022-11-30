package com.simplecrmapi.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

//Employees of various roles can be users
@Entity
public class User implements UserDetails{
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private int id;
	
	@Column
	private String username;
	
	@Column
	@JsonIgnore
	private String password;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "USER_ROLES",
				joinColumns = {
						@JoinColumn(name="USER_ID")
				},
				inverseJoinColumns = {
						@JoinColumn(name="ROLE_ID")
				})
	private Set<Role> roles;
	
	@Column
	private boolean enabled = true;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="employee_id")
	private Employee employee;

	public User() {
		
	}
	
	public User(String username, String password, Set<Role> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	
	//Test constructor please ignore
	public User(int id, String username, String password, Set<Role> roles) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.roles = roles;
	}

	//also a test constructor
	public User(int id, String username, String password, Set<Role> roles, boolean enabled, Employee employee) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.enabled = enabled;
		this.employee = employee;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		final Set<SimpleGrantedAuthority> auths = new HashSet<SimpleGrantedAuthority>();
		
		if(roles!=null) {
			for(Role role:this.roles) {
				auths.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
			}
		}
		
		return auths;

	}

	//Force true for now, db fields later
	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}
}
