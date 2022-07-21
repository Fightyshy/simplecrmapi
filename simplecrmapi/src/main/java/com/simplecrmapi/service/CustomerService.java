package com.simplecrmapi.service;

import java.util.List;

import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.entity.SocialMedia;

public interface CustomerService {
	public List<Customer> getCustomers();
	
	public Customer getCustomerByID(Integer ID);
	
	public List<Customer> getCustomerByLastName(String lastName);
	
	public List<Customer> getCustomerByFirstName(String firstName);
	
	public Customer saveCustomerDetails(Customer customer);
	
	public void deleteCustomerByID(Integer ID);

	public void deleteCustomerSocialMediaByID(Integer ID);

	public void deleteCustomerAddressByID(Integer CustomerID, Integer AddressID);

	public SocialMedia updateCustomerSocialMedia(SocialMedia socialMedia, Integer ID);

	public Address updateCustomerAddressByID(Address address);

	public Address saveAddressToCustomer(Address address, Integer ID);

	public List<Customer> getUserCustomersFromCases(Employee emp);

	public Customer updateUserCustomerByID(Customer customer, Employee employee);

	public Address updateUserAssignedAdressByID(Address address, Employee employee);

	public SocialMedia updateUserAssignedSocialMediaByID(SocialMedia socialMedia, Employee employee);
}
