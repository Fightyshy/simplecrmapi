package com.simplecrmapi.dao;

import java.util.List;

import com.simplecrmapi.entity.Cases;

public interface CasesDAO {
	public List<Cases> getCases();
	
	public Cases getCaseByID(Integer ID);
	
	public List<Cases> getCasesByEmployee(Integer employeeID);
	
	public List<Cases> getCasesByCustomer(Integer customerID);
	
	public Cases saveCase(Cases casee);
	
	public void deleteCaseByID(Integer ID);
}
