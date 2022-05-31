package com.simplecrmapi.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.util.EntityNotFound;
import com.simplecrmapi.util.InvalidParamsException;

@Repository
public class CustomerDAOImpl implements CustomerDAO {

	private EntityManager entityManager;
	
	public CustomerDAOImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Customer> getCustomers() {
		Query getCustomers = entityManager.createQuery("from Customer");
		return getCustomers.getResultList();
	}

	@Override
	public Customer getCustomerByID(Integer ID) {
		try {
			Customer customer = entityManager.find(Customer.class, ID);
			return customer;
		}catch(IllegalArgumentException e) {
			e.printStackTrace();
//			System.out.println(">>> An exception due to invalid arguements has occurred, returning null");
			throw new InvalidParamsException();
		}
	}

	@Override
	public List<Customer> getCustomerByLastName(String lastName) {
		try {
			Query getCustomers = entityManager.createQuery("from Customer where lastName=:customerLastName");
			getCustomers.setParameter("customerLastName", lastName);
			return getCustomers.getResultList();
		}catch(Exception e) {
			return null;
		}
	}

	@Override
	public List<Customer> getCustomerByFirstName(String firstName) {
		try {
			Query getCustomers = entityManager.createQuery("from Customer where firstName=:customerfirstName");
			getCustomers.setParameter("customerfirstName", firstName);
			return getCustomers.getResultList();
		}catch(Exception e) {
			return null;
		}
	}

	@Override
	public Customer saveCustomerDetails(Customer customer) {
		
//		//Gauntlet check
//		if(customer.getId()==null) {
//			throw new EntityNotFound();
//		}
		try {
			//merge to save new/update old
			if(customer.getId()==0) {
				entityManager.persist(customer);
				return customer;
			}else if(customer.getId()!=0) {
				Customer dbCustomer = entityManager.merge(customer);
				return dbCustomer;
			}
			
			return null;
		}catch(NullPointerException npe) {
			throw new EntityNotFound();
		}catch(EntityNotFoundException enfe) {
			throw new EntityNotFound();
		}
//		Customer dbCustomer = entityManager.merge(customer);
		//set arg id to db id for save/update ops
//		customer.setId(dbCustomer.getId());
//		return dbCustomer;
	}

	@Override
	public void deleteCustomerByID(Integer ID) {
		Query deleteCustomer = entityManager.createQuery("delete from Customer where id=:customerID");
		deleteCustomer.setParameter("customerID", ID);
		deleteCustomer.executeUpdate();
	}

	@Override
	public void deleteCustomerSocialMediaByID(Integer ID) {
		Customer cus = getCustomerByID(ID);
		cus.setSocialMedia(new SocialMedia());
		entityManager.merge(cus);
	}
	
	@Override
	public void deleteCustomerAddressByID(Integer customerID, Integer addressID) {
		Query deleteAddress = entityManager.createQuery("delete from Address a where a.id=:addressID and a.customer.id=:customerID");
		deleteAddress.setParameter("customerID", customerID);
		deleteAddress.setParameter("addressID", addressID);
		deleteAddress.executeUpdate();
	}
}
