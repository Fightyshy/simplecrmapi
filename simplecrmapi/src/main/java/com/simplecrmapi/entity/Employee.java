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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="employee")
public class Employee extends Person{
	
	//Metrics below
	@Column(name="cases_active")
	@PositiveOrZero
	private Integer casesActive;
	
	@Column(name="cases_pending")
	@PositiveOrZero
	private Integer casesPending;
	
	@Column(name="cases_resolved")
	@PositiveOrZero
	private Integer casesResolved;
	
	@Column(name="cases_closed")
	@PositiveOrZero
	private Integer casesClosed;
	
	@OneToMany(mappedBy="employee",cascade= {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
	private List<Address> address;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="social_media_id")
	private SocialMedia socialMedia;
	
	//TODO properly shift oneTomanys to appropriate POJOs and make appropriate db fixes
	//Customers here redundant, see new dataflow plan

	@JsonIgnore
	@ManyToMany(mappedBy="employee",cascade= {CascadeType.DETACH,CascadeType.REFRESH,CascadeType.REMOVE})
	private Set<Cases> cases;
	
	public Employee() {
	}

	public Employee(@NotNull @NotEmpty String firstName, String middleName, @NotNull @NotEmpty String lastName,
			@NotNull @NotEmpty LocalDate dateOfBirth, String phoneNumber, String emailAddress, Integer casesActive, Integer casesPending, Integer casesResolved, Integer casesClosed) {
		super(firstName, middleName, lastName, dateOfBirth, phoneNumber, emailAddress);
		this.casesActive = casesActive;
		this.casesPending = casesPending;
		this.casesResolved = casesResolved;
		this.casesClosed = casesClosed;
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

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public SocialMedia getSocialMedia() {
		return socialMedia;
	}

	public void setSocialMedia(SocialMedia socialMedia) {
		this.socialMedia = socialMedia;
	}

	public Set<Cases> getCases() {
		return cases;
	}

	public void setCases(Set<Cases> cases) {
		this.cases = cases;
	}
}
