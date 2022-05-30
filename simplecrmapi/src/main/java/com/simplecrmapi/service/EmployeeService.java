package com.simplecrmapi.service;

import java.util.List;

import javax.validation.Valid;

import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.entity.SocialMedia;

public interface EmployeeService {

	public List<Employee> getEmployees();
	
	public Employee getEmployeeByID(Integer ID);
	
	public Employee  saveEmployeeDetails(Employee employee);
	
	public void deleteEmployeeByID(Integer ID);

	public List<Customer> getCustomersAssignedToEmployee(int ID);

	public List<Cases> getEmployeeCasesByID(int ID);

	public Customer getCustomerFromEmployeeAssignedCase(int empID, int caseID);

	public Cases saveNewCaseToEmployee(Cases cases, Integer ID);

	public Address saveNewAddressToEmployee(Address address, Integer ID);

	public Address updateEmployeeAddressByID(Address address, Integer ID);

	public Cases updateEmployeeAssignedCaseByID(Cases cases, Integer ID);

	public SocialMedia updateEmployeeSocialMedia(@Valid SocialMedia socialMedia, Integer ID);

	public void deleteEmployeeAddressByIDs(int employeeID, int addressID);

	public void removeEmployeeAssignedCase(int employeeID, int caseID);
}
