package com.simplecrmapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
		return customerDAO.findAll();
	}

	@Override
	@Transactional
	public Customer getCustomerByID(Integer ID) {
		try {
			return customerDAO.findById(ID).orElseGet(null);
		}catch(Exception e) {
			return null;
		}
	}

	@Override
	@Transactional
	public List<Customer> getCustomerByLastName(String lastName) {
		return customerDAO.findByLastName(lastName);
	}

	@Override
	@Transactional
	public List<Customer> getCustomerByFirstName(String firstName) {
		return customerDAO.findByFirstName(firstName);
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
	//Dual function save/update
	public Customer saveCustomerDetails(Customer customer) {
		Customer inputCus = customer;
		Customer fromMemory = getCustomerByID(customer.getId());
		if(fromMemory==null) {
			inputCus.setId(0); //new
		}else {
			inputCus.setId(fromMemory.getId());
		}
		
		if(inputCus.getAddress().isEmpty()||inputCus.getAddress()==null) {
			inputCus.setAddress(new ArrayList<Address>());
		}else {
			for(Address add: inputCus.getAddress()) {
				add.setCustomer(inputCus);
				addressDAO.saveAndFlush(add);
			}
		}
		
		return customerDAO.saveAndFlush(inputCus);
	}
	
	@Override
	@Transactional
	public Address saveAddressToCustomer(Address address, Integer ID) {
		Customer addAddress = getCustomerByID(ID);
		addAddress.getAddress().add(address);
		
		addAddress = customerDAO.saveAndFlush(addAddress);
		addressDAO.saveAndFlush(address);
		return address;
	}
	
	@Override
	@Transactional
	public Customer updateUserCustomerByID(Customer customer, Employee employee) {
		for(Cases cases:employee.getCases()) {
			if(comparator.EqualsID(cases.getCustomer(), customer)) {
				return customerDAO.saveAndFlush(customer);
				
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
					addressDAO.saveAndFlush(address);
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
				customerDAO.saveAndFlush(cus);
				return socialMedia;
			}
		}
		return null;
	}

	@Override
	@Transactional
	public SocialMedia updateCustomerSocialMedia(SocialMedia socialMedia, Integer ID) {
		try {
			Customer cus = customerDAO.findById(ID).get();
			cus.setSocialMedia(socialMedia);
			cus = customerDAO.saveAndFlush(cus);
			return cus.getSocialMedia();
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	@Transactional
	public Address updateCustomerAddressByID(Address address) {
		return addressDAO.saveAndFlush(address);
	}
	
	@Override
	@Transactional
	public void deleteCustomerByID(Integer ID) {
		customerDAO.deleteById(ID);
	}
	
	@Override
	@Transactional
	public void deleteCustomerSocialMediaByID(Integer ID) {
		try {
			Customer cus = customerDAO.findById(ID).get();
			cus.setSocialMedia(new SocialMedia());
			customerDAO.saveAndFlush(cus);
		}catch(NoSuchElementException e) {
			System.out.println("ID of customer not found");
		}
	}
	
	@Override
	@Transactional
	public void deleteCustomerAddressByID(Integer CustomerID, Integer AddressID) {
		try {
			customerDAO.deleteCustomerAddressByID(CustomerID, AddressID);
		}catch(Exception e) {
			
		}
	}

}
