package com.simplecrmapi.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.simplecrmapi.entity.Employee;

public interface EmployeeDAO extends JpaRepository<Employee, Integer>{

	Employee findByEmailAddress(String emailAddress);
	
	@Query("DELETE FROM Address a WHERE a.id=:addressID AND a.employee.id=:employeeID")
	public void deleteEmployeeAddressByIDs(int employeeID, int addressID);
	
	@Query("SELECT distinct e,c from User u left join u.employee e left join e.cases c WHERE u.username=:username")
	public Employee findEmployeeByUsername(String username);
}
