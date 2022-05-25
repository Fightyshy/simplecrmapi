package com.simplecrmapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplecrmapi.dao.AddressDAO;
import com.simplecrmapi.dao.CustomerDAO;
import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.util.CustomerInvalidAddressException;
import com.simplecrmapi.util.CustomerInvalidSocialMediaException;
import com.simplecrmapi.util.EntityNotFound;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerDAO customerDAO;
	
	@Autowired
	private AddressDAO addressDAO;
	
	@Override
	@Transactional
	public List<Customer> getCustomers() {
		return customerDAO.getCustomers();
	}

	@Override
	@Transactional
	public Customer getCustomerByID(Integer ID) {
		if(ID==null) {
			throw new EntityNotFound();
		}
		return customerDAO.getCustomerByID(ID);
	}

	@Override
	@Transactional
	public List<Customer> getCustomerByLastName(String lastName) {
		try {
			List<Customer> lastNames = customerDAO.getCustomerByLastName(lastName);
			return lastNames;
		}catch(Exception e){
			System.out.println(">>> Last names not found or list broken, returning null");
			return null; //temp measure
		}
	}

	@Override
	@Transactional
	public List<Customer> getCustomerByFirstName(String firstName) {
		try {
			List<Customer> lastNames = customerDAO.getCustomerByLastName(firstName);
			return lastNames;
		}catch(Exception e){
			System.out.println(">>> First names not found or list broken, returning null");
			return null; //temp measure
		}
	}

	@Override
	@Transactional
	public Customer saveCustomerDetails(Customer customer) {
		
		//TODO trycatch cleanup
		Customer newCustomer;
		if(customer.getId()==null) {
			throw new EntityNotFound();
		}else if((customer.getAddress().isEmpty()||customer.getAddress().equals(null))&&(customer.getSocialMedia()==null||!customer.getSocialMedia().getPreferredSocialMedia().equals("NO_PREFERENCE"))) {
			
		}else if(customer.getAddress().isEmpty()||customer.getAddress().equals(null)) {
			throw new CustomerInvalidAddressException();
		}else if(customer.getSocialMedia()==null||!customer.getSocialMedia().getPreferredSocialMedia().equals("NO_PREFERENCE")) {
			throw new CustomerInvalidSocialMediaException();
		}
		
		if(customer.getAddress().isEmpty()||customer.getAddress()==null) {
			newCustomer = customerDAO.saveCustomerDetails(customer);
			return newCustomer; //TODO customer should be modified if needed			
		}else {
			newCustomer = customerDAO.saveCustomerDetails(customer);
			for(Address add:customer.getAddress()) {
				add.setCustomer(newCustomer); //It works shutup
				addressDAO.save(add);
			}
			return newCustomer;
		}
	}
	
	@Override
	@Transactional
	public Address saveAddressToCustomer(Address address, Integer ID) {
		Customer addAddress = getCustomerByID(ID);
		addAddress.getAddress().add(address);
		
		addAddress = customerDAO.saveCustomerDetails(addAddress);
		addressDAO.save(address);
		return address;
	}

	@Override
	@Transactional
	public SocialMedia updateCustomerSocialMedia(SocialMedia socialMedia, Integer ID) {
		try {
		Customer getCustomerFromID = customerDAO.getCustomerByID(ID);
		System.out.println(">>>"+getCustomerFromID.getId());
		getCustomerFromID.setSocialMedia(socialMedia);
		getCustomerFromID = customerDAO.saveCustomerDetails(getCustomerFromID);
		return getCustomerFromID.getSocialMedia();
		}catch(NullPointerException e){
			return null;
		}
	}
	
	@Override
	@Transactional
	public Address updateCustomerAddressByID(Address address) {
		
		return addressDAO.save(address);
	}
	
	@Override
	@Transactional
	public void deleteCustomerByID(Integer ID) {
		customerDAO.deleteCustomerByID(ID);
	}
	
	@Override
	@Transactional
	public void deleteCustomerSocialMediaByID(Integer ID) {
		customerDAO.deleteCustomerSocialMediaByID(ID);
	}
	
	@Override
	@Transactional
	public void deleteCustomerAddressByID(Integer CustomerID, Integer AddressID) {
		customerDAO.deleteCustomerAddressByID(CustomerID, AddressID);
	}

}
