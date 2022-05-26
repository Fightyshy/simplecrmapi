package com.simplecrmapi.service;

import java.util.List;

import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;

public interface EmployeeService {

	public List<Employee> getEmployees();
	
	public Employee getEmployeeByID(Integer ID);
	
	public Employee  saveEmployeeDetails(Employee employee);
	
	public void deleteEmployeeByID(Integer ID);

	public List<Customer> getCustomersAssignedToEmployee(int ID);

	public List<Cases> getEmployeeCasesByID(int ID);

	public Customer getCustomerFromEmployeeAssignedCase(int empID, int caseID);
}
