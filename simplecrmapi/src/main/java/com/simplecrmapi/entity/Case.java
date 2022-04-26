package com.simplecrmapi.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.simplecrmapi.enums.CaseStatus;

@Entity
@Table(name="case")
public class Case {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(name="case_status")
	@NotNull
	private String caseStatus; //Not null
	
	@Column(name="start_date")
	@NotNull
	private LocalDateTime startDate; //Not null
	
	@Column(name="end_date")
	private LocalDateTime endDate;
	
	@Column(name="product")
	private String product;
	
	@ManyToOne
	
	private List<Employee> employee;
	
	@OneToOne
	@JoinColumn(name="customer_id")
	private Customer customer;
	
	public Case() {
		this.caseStatus = CaseStatus.PENDING.toString();
		this.startDate = LocalDateTime.now();
	}
	
}
