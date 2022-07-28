package com.simplecrmapi.service;

import java.util.List;

import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;

public interface CasesService {
	public List<Cases> getAllCases();
	
	public List<Cases> getCasesByLastName(String lastName);
	
	public List<Cases> getCasesByFirstName( String firstName);
	
	public Cases getCaseByID(Integer ID);
	
	public Cases updateCase(Cases cases);

	public Cases updateCase(Cases cases, Employee employee);

	public Cases saveEmployeeToCase(Cases cases, Integer empID);

	public void deleteCaseByID(Integer ID);

	public void deleteEmployeeFromCase(Integer ID, Integer empID);

	public List<Customer> getCustomersFromCaseProducts(String product);

	public void deleteCasesWithDiscontinuedProducts(String product);

	public Cases saveNewCase(Cases cases, String product, Integer empID);
}
