package com.simplecrmapi.entity;


public class AddEmployeeDTO {
//	private int caseID;
	
	private Cases cases;
	
	private Employee employee;

	public AddEmployeeDTO(Cases cases, Employee employee) {
//		this.caseID = caseID;
		this.cases = cases;
		this.employee = employee;
	}

	public AddEmployeeDTO() {
	}

//	public int getCaseID() {
//		return caseID;
//	}
//
//	public void setCaseID(int caseID) {
//		this.caseID = caseID;
//	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Cases getCases() {
		return cases;
	}

	public void setCases(Cases cases) {
		this.cases = cases;
	}
	
}
