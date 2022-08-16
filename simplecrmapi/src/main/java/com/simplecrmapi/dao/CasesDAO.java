package com.simplecrmapi.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;

public interface CasesDAO extends JpaRepository<Cases, Integer>{
	@Query("SELECT FROM Cases c WHERE c.employee.id=:empID")
	List<Cases> findCasesByEmployee(@Param("empID") Integer employeeID);
	
	@Query("SELECT FROM Cases c WHERE c.customer.id=:cusID")
	List<Cases> findCasesByCustomer(@Param("cusID") Integer customerID);
	
	@Query("SELECT FROM Cases c WHERE c.customer.firstname=:firstname")
	List<Cases> findCasesByCustomerFirstName(@Param("firstname") String firstName);

	@Query("SELECT FROM Cases c WHERE c.customer.lastname=:lastname")
	List<Cases> findCasesByCustomerLastName(@Param("lastname") String lastName);
	
	@Query("SELECT FROM Cases c WHERE c.id=:caseID AND c.employee.id=:empID")
	Customer findCustomerFromEmployeeAssignedCase(@Param("empID") Integer employeeID, @Param("caseID") Integer caseID);
	
	@Query("DELETE FROM Cases WHERE c.product.id=:prodID")
	void deleteByProduct(@Param("prodID") Integer productID);
}
