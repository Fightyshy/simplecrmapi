package com.simplecrmapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplecrmapi.dao.AddressDAO;
import com.simplecrmapi.dao.CustomerDAO;
import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.util.CustomerInvalidAddressException;
import com.simplecrmapi.util.CustomerInvalidObjectsException;
import com.simplecrmapi.util.CustomerInvalidSocialMediaException;
import com.simplecrmapi.util.EntityNotFound;
import com.simplecrmapi.util.IDComparator;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerDAO customerDAO;
	
	@Autowired
	private AddressDAO addressDAO;
	
	private IDComparator comparator;
	
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
			List<Customer> firstNames = customerDAO.getCustomerByFirstName(firstName);
			return firstNames;
		}catch(Exception e){
			System.out.println(">>> First names not found or list broken, returning null");
			return null; //temp measure
		}
	}
	
	@Override
	@Transactional
	public List<Customer> getUserCustomersFromCases(Employee emp){
		List<Customer> customerList = new ArrayList<>();
		
		emp.getCases().iterator().forEachRemaining(cas->customerList.add(cas.getCustomer()));
		return customerList;
	}

	@Override
	@Transactional
	public Customer saveCustomerDetails(Customer customer) {
		
		//TODO trycatch cleanup
		Customer newCustomer;
		if(customer.getId()==null) {
			throw new EntityNotFound();
		}else if((customer.getAddress().isEmpty()||customer.getAddress().equals(null))&&(customer.getSocialMedia()==null||!customer.getSocialMedia().getPreferredSocialMedia().equals("NO_PREFERENCE"))) {
			throw new CustomerInvalidObjectsException();
		}else if(customer.getAddress().isEmpty()||customer.getAddress().equals(null)) {
			throw new CustomerInvalidAddressException();
		}else if(customer.getSocialMedia()==null||!customer.getSocialMedia().getPreferredSocialMedia().equals("NO_PREFERENCE")) {
			throw new CustomerInvalidSocialMediaException();
		}
		
		//TODO ????
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
	public Customer updateUserCustomerByID(Customer customer, Employee employee) {
		for(Cases cases:employee.getCases()) {
			if(comparator.EqualsID(cases.getCustomer(), customer)) {
				return customerDAO.saveCustomerDetails(customer);
				
			}
		}
		return null;
	}
	
	@Override
	@Transactional
	public Address updateUserAssignedAdressByID(Address address, Employee employee) {
		for(Cases cases: employee.getCases()) {
			Customer cus = cases.getCustomer();
			for(Address add: cus.getAddress()) {
				if(add.getId()==address.getId()) {
					cus.getAddress().remove(add);
					cus.getAddress().add(address);
					address.setCustomer(cus);
					addressDAO.save(address);
					return address;
				}
			}
		}
		return null;
	}
	
	@Override
	@Transactional
	public SocialMedia updateUserAssignedSocialMediaByID(SocialMedia socialMedia, Employee employee) {
		for(Cases cases:employee.getCases()) {
			Customer cus = cases.getCustomer();
			if(cus.getSocialMedia().getId()==socialMedia.getId()) {
				cus.setSocialMedia(socialMedia);
				customerDAO.saveCustomerDetails(cus);
				return socialMedia;
			}
		}
		return null;
	}

	@Override
	@Transactional
	public SocialMedia updateCustomerSocialMedia(SocialMedia socialMedia, Integer ID) {
		try {
		Customer getCustomerFromID = customerDAO.getCustomerByID(ID);
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
