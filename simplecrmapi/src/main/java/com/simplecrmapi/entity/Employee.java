package com.simplecrmapi.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="employee")
public class Employee {
	
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
	
	@OneToMany(cascade= {CascadeType.DETACH})
	@JoinColumn(name="customer_id")
	private List<Customer> customers;

	@OneToMany
	@JoinColumn(name="case_id")
	private List<Case> cases;
	
	public Employee() {
	}

	public Employee(Integer casesActive, Integer casesPending, Integer casesResolved, Integer casesClosed,
			List<Address> addresses, List<Customer> customers) {
		super();
		this.casesActive = casesActive;
		this.casesPending = casesPending;
		this.casesResolved = casesResolved;
		this.casesClosed = casesClosed;
		this.addresses = addresses;
		this.customers = customers;
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
	
	
}
