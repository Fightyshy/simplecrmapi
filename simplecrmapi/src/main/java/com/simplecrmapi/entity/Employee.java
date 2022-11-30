package com.simplecrmapi.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="employee")
public class Employee extends Person{
	
	//Metrics below
	@Column(name="cases_active")
	@PositiveOrZero(message="Please input a number greater than or equal to 0")
	private Integer casesActive;
	
	@Column(name="cases_pending")
	@PositiveOrZero(message="Please input a number greater than or equal to 0")
	private Integer casesPending;
	
	@Column(name="cases_resolved")
	@PositiveOrZero(message="Please input a number greater than or equal to 0")
	private Integer casesResolved;
	
	@Column(name="cases_closed")
	@PositiveOrZero(message="Please input a number greater than or equal to 0")
	private Integer casesClosed;

	@JsonIgnore
	@ManyToMany(mappedBy="employee",cascade= {CascadeType.DETACH,CascadeType.REFRESH,CascadeType.REMOVE})
	private Set<Cases> cases;
	
	@OneToMany(mappedBy="employee",cascade= {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
	private List<Address> address;

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public Employee(Integer id, @NotNull @NotEmpty String firstName, String middleName,
			@NotNull @NotEmpty String lastName,
			@NotNull @Past(message = "Date has to be in the past") LocalDate dateOfBirth, String phoneNumber,
			@Email(message = "Please input a valid email address") String emailAddress,
			@PositiveOrZero(message = "Please input a number greater than or equal to 0") Integer casesActive,
			@PositiveOrZero(message = "Please input a number greater than or equal to 0") Integer casesPending,
			@PositiveOrZero(message = "Please input a number greater than or equal to 0") Integer casesResolved,
			@PositiveOrZero(message = "Please input a number greater than or equal to 0") Integer casesClosed) {
		super(id, firstName, middleName, lastName, dateOfBirth, phoneNumber, emailAddress);
		this.casesActive = casesActive;
		this.casesPending = casesPending;
		this.casesResolved = casesResolved;
		this.casesClosed = casesClosed;
	}

	public Employee() {
	}

	public Integer getCasesActive() {
		return casesActive;
	}

	public void setCasesActive(Integer casesActive) {
		this.casesActive = casesActive;
	}

	public Integer getCasesPending() {
		return casesPending;
	}

	public void setCasesPending(Integer casesPending) {
		this.casesPending = casesPending;
	}

	public Integer getCasesResolved() {
		return casesResolved;
	}

	public void setCasesResolved(Integer casesResolved) {
		this.casesResolved = casesResolved;
	}

	public Integer getCasesClosed() {
		return casesClosed;
	}

	public void setCasesClosed(Integer casesClosed) {
		this.casesClosed = casesClosed;
	}

	public Set<Cases> getCases() {
		return cases;
	}

	public void setCases(Set<Cases> cases) {
		this.cases = cases;
	}
}
