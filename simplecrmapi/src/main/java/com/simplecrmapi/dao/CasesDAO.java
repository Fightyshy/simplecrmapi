package com.simplecrmapi.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;

public interface CasesDAO extends JpaRepository<Cases, Integer>{
	@Query("select c from Cases c join c.employee e WHERE e.id=:empID")
	List<Cases> findCasesByEmployee(@Param("empID") Integer employeeID);
	
	@Query("FROM Cases c WHERE c.customer.id=:cusID")
	List<Cases> findCasesByCustomer(@Param("cusID") Integer customerID);
	
	@Query("FROM Cases c WHERE c.customer.firstName=:firstname")
	List<Cases> findCasesByCustomerFirstName(@Param("firstname") String firstName);

	@Query("from Cases c where c.customer.lastName=:lastname")
	List<Cases> findCasesByCustomerLastName(@Param("lastname") String lastName);
	
	@Query("SELECT c FROM Cases c JOIN c.employee e WHERE c.id=:caseID AND e.id=:empID")
	Customer findCustomerFromEmployeeAssignedCase(@Param("empID") Integer employeeID, @Param("caseID") Integer caseID);
	
	@Query("DELETE FROM Cases c WHERE c.product.id=:prodID")
	void deleteByProduct(@Param("prodID") Integer productID);
}
