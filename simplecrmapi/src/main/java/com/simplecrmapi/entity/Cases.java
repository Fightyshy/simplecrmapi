package com.simplecrmapi.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="cases")
@JsonIdentityInfo(
		scope = Cases.class,
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class Cases {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="cases_status")
	@NotNull
	private String casesStatus; //Not null
	
	//No validator because set by app and not human
	@Column(name="start_date")
	@NotNull
	private LocalDateTime startDate; //Not null
		
	//Should be set by computer
	@Column(name="end_date")
	private LocalDateTime endDate;
	
//	TODO
	@Column(name="summary")
	private String summary;
	
	@OneToOne(cascade= {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
	@JoinColumn(name="products_id")
	private Product product;
	
	//TODO get this working
//	@JsonIgnore //Jsonview this so it doesn't show when querying employee
	@ManyToMany
//	@JsonIdentityReference(alwaysAsId = true)
	@JoinTable(
			name="employee_cases",
			joinColumns=@JoinColumn(name="cases_id"),
			inverseJoinColumns=@JoinColumn(name="employee_id"))
	private Set<Employee> employee;
	
	@OneToOne
	@JoinColumn(name="customer_id")
	private Customer customer;
	
	public Cases() {
		this.casesStatus = "PENDING";
		this.startDate = LocalDateTime.now();
	}
	
	public Cases(@NotNull String casesStatus, String summary, Product product, Customer customer) {
		this.casesStatus = casesStatus;
		this.startDate = LocalDateTime.now();
		this.endDate = null;
		this.summary = summary;
		this.product  = product;
		this.customer = customer;
	}
	
	
	public Cases(Integer id, @NotNull String casesStatus, @NotNull LocalDateTime startDate, LocalDateTime endDate,
			String summary, Product product, Set<Employee> employee, Customer customer) {
		this.id = id;
		this.casesStatus = casesStatus;
		this.startDate = startDate;
		this.endDate = endDate;
		this.summary = summary;
		this.product = product;
		this.employee = employee;
		this.customer = customer;
	}
	
//	public Cases() {
//		this.casesStatus = CaseStatus.PENDING.toString();
//		this.startDate = LocalDateTime.now();
//	}
//	
//	public Cases(@NotNull String casesStatus, Product product, Customer customer) {
//		this.casesStatus = casesStatus;
//		this.startDate = LocalDateTime.now();
//		this.endDate = null;
//		this.product  = product;
//		this.customer = customer;
//	}
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCasesStatus() {
		return casesStatus;
	}

	public void setCasesStatus(String casesStatus) {
		this.casesStatus = casesStatus;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Set<Employee> getEmployee() {
		return employee;
	}

	public void setEmployee(Set<Employee> employee) {
		this.employee = employee;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public boolean equals(Object cases) {
		if(cases.getClass()!=this.getClass()) {
			return false;
		}
		
		Cases cased = (Cases) cases;
		
		if(cased.casesStatus!=this.casesStatus) {
			return false;
		}
		if(cased.startDate!=this.startDate) {
			return false;
		}
		if(cased.endDate!=this.endDate) {
			return false;
		}
		if(cased.product!=this.product) {
			return false;
		}
		if(!cased.customer.equals(this.customer)) {
			return false;
		}
		if(!cased.employee.equals(this.employee)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Cases [id=" + id + ", casesStatus=" + casesStatus + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", desc=" + summary + ", product=" + product + ", employee=" + employee + ", customer=" + customer + "]";
	}
	
	
}
