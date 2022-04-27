package com.simplecrmapi.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="employee")
public class Employee extends Person{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	//Metrics below
	@Column(name="cases_active")
	private Integer casesActive;
	
	@Column(name="cases_pending")
	private Integer casesPending;
	
	@Column(name="cases_resolved")
	private Integer casesResolved;
	
	@Column(name="cases_closed")
	private Integer casesClosed;
	
	@OneToMany(cascade= {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
	@JoinColumn(name="address_id")
	private List<Address> addresses;
	
	@OneToOne(cascade= {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
	@JoinColumn(name="social_media_id")
	private SocialMedia socialMedia;
	
	@OneToMany(cascade= {CascadeType.DETACH})
	@JoinColumn(name="customer_id")
	private List<Customer> customers;

	@OneToMany
	@JoinColumn(name="case_id")
	private List<Case> cases;
	
	public Employee() {
	}

	public Employee(@NotNull @NotEmpty String firstName, String middleName, @NotNull @NotEmpty String lastName,
			@NotNull @NotEmpty LocalDate dateOfBirth, Integer phoneNumber, String emailAddress, Integer casesActive, Integer casesPending, Integer casesResolved, Integer casesClosed) {
		super(firstName, middleName, lastName, dateOfBirth, phoneNumber, emailAddress);
		this.casesActive = casesActive;
		this.casesPending = casesPending;
		this.casesResolved = casesResolved;
		this.casesClosed = casesClosed;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public SocialMedia getSocialMedia() {
		return socialMedia;
	}

	public void setSocialMedia(SocialMedia socialMedia) {
		this.socialMedia = socialMedia;
	}

	public List<Case> getCases() {
		return cases;
	}

	public void setCases(List<Case> cases) {
		this.cases = cases;
	}
	
	
}
