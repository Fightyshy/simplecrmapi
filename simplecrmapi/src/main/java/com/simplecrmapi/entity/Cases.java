package com.simplecrmapi.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simplecrmapi.enums.CaseStatus;

@Entity
@Table(name="cases")
public class Cases {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="cases_status")
	@NotNull
	private String casesStatus; //Not null
	
	@Column(name="start_date")
	@NotNull
	private LocalDateTime startDate; //Not null
	
	@Column(name="end_date")
	private LocalDateTime endDate;
	
	@Column(name="product")
	private String product;
	
	//TODO get this working
	@JsonIgnore //Jsonview this so it doesn't show when querying employee
	@ManyToMany
	@JoinTable(
			name="employee_cases",
			joinColumns=@JoinColumn(name="cases_id"),
			inverseJoinColumns=@JoinColumn(name="employee_id"))
	private Set<Employee> employee;
	
	@OneToOne
	@JoinColumn(name="customer_id")
	private Customer customer;
	
	public Cases() {
		this.casesStatus = CaseStatus.PENDING.toString();
		this.startDate = LocalDateTime.now();
	}

	public Cases(@NotNull String casesStatus, @NotNull LocalDateTime startDate, LocalDateTime endDate, String product, Customer customer) {
		super();
		this.casesStatus = casesStatus;
		this.startDate = startDate;
		this.endDate = endDate;
		this.product = product;
		this.customer = customer;
	}

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

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
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
	
}
