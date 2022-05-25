package com.simplecrmapi.dao;

import java.util.List;

import com.simplecrmapi.entity.Customer;

public interface CustomerDAO {
	public List<Customer> getCustomers();
	
	public Customer getCustomerByID(Integer ID);
	
	public List<Customer> getCustomerByLastName(String lastName);
	
	public List<Customer> getCustomerByFirstName(String firstName);
	
	public Customer saveCustomerDetails(Customer customer);
	
	public void deleteCustomerByID(Integer ID);

	public void deleteCustomerSocialMediaByID(Integer ID);

	public void deleteCustomerAddressByID(Integer customerID, Integer addressID);
}
