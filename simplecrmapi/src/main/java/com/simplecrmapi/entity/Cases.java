package com.simplecrmapi.entity;

import java.time.LocalDateTime;
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
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import com.simplecrmapi.enums.CaseStatus;
import com.simplecrmapi.validation.Alphanumeric;

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
	
	//No validator because set by app and not human

	@Column(name="end_date")
	private LocalDateTime endDate;
	
	@Column(name="product")
	@Alphanumeric(message="Please input only alphanumeric characters")
	private String product;
	
	//TODO get this working
//	@JsonIgnore //Jsonview this so it doesn't show when querying employee
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
	
	public Cases(@NotNull String casesStatus, String product, Customer customer) {
		this.casesStatus = casesStatus;
		this.startDate = LocalDateTime.now();
		this.endDate = null;
		this.product  = product;
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
	
	public boolean equalsID(Cases cases) {
		if(cases.getId()==this.id) {
			return true;
		}else {
			return false;
		}
	}
}
