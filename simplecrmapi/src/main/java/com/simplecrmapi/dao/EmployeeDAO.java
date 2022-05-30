package com.simplecrmapi.dao;

import java.util.List;

import com.simplecrmapi.entity.Employee;

public interface EmployeeDAO {
	public List<Employee> getEmployees();
	
	public Employee getEmployeeByID(Integer ID);
	
	public Employee saveEmployee(Employee employee);

	public void deleteEmployee(Integer ID);

	public void deleteEmployeeAddressByIDs(int employeeID, int addressID);
}
