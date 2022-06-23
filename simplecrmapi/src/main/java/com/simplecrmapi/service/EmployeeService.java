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

	public List<Customer> getCustomersAssignedToEmployee(Employee emp);

	public List<Cases> getEmployeeCasesByID(int ID);

	public Customer getCustomerFromEmployeeAssignedCase(int empID, int caseID);

	public Customer getCustomerFromEmployeeAssignedCase(Employee emp, int caseID);
	
	public Cases saveNewCaseToEmployee(Cases cases, Integer ID);

	public Cases saveNewCaseToEmployee(Cases cases, Employee emp);
	
	public Address saveNewAddressToEmployee(Address address, Integer ID);

	public Address saveNewAddressToEmployee(Address address, Employee emp);
	
	public Cases updateEmployeeAssignedCaseByID(Cases cases, Integer ID);

	public Cases updateEmployeeAssignedCaseByID(Cases cases, Employee emp);
	
	public SocialMedia updateEmployeeSocialMedia(@Valid SocialMedia socialMedia, Integer ID);

	public SocialMedia updateEmployeeSocialMedia(@Valid SocialMedia socialMedia, Employee emp);
	
	public Address updateEmployeeAddressByID(Address address, Integer ID);

	public Address updateEmployeeAddressByID(Address address, Employee emp);
	
	public void deleteEmployeeAddressByIDs(int employeeID, int addressID);

	public void deleteEmployeeAddressByIDs(Employee emp, int addressID);

	public void removeEmployeeAssignedCase(int employeeID, int caseID);
}
