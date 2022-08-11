package com.simplecrmapi.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.simplecrmapi.entity.Customer;

public interface CustomerDAO extends JpaRepository<Customer, Integer> {
	List<Customer> findByLastName(String lastName);
	
	List<Customer> findByFirstName(String firstName);
	
	@Query("DELETE FROM Address a WHERE a.id=:addressID AND a.customer.id=:customerID")
	void deleteCustomerAddressByID(@Param("addressID") Integer addressID, @Param("customerID") Integer customerID);
}
