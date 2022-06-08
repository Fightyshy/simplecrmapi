package com.simplecrmapi.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;

@Repository
public class CasesDAOImpl implements CasesDAO {

	private EntityManager entityManager;
	
	public CasesDAOImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Override
	public List<Cases> getCases() {
		Query getCases = entityManager.createQuery("from Cases",Cases.class);
		return getCases.getResultList();
	}

	@Override
	public Cases getCaseByID(Integer ID) {
		try {
			Cases cases = entityManager.find(Cases.class, ID);
			return cases;
		}catch(IllegalArgumentException e) {
			e.printStackTrace();
//			System.out.println(">>> An exception due to invalid arguements has occurred, returning null");
			return null;
		}
	}
	
	@Override
	public List<Cases> getCasesByCustomerFirstName(String firstName){
		Query getCases = entityManager.createQuery("from Cases c where c.customer.firstName=:paramFirstName");
		getCases.setParameter("paramFirstName", firstName);
		return getCases.getResultList();
	}
	
	@Override
	public List<Cases> getCasesByCustomerLastName(String lastName){
		Query getCases = entityManager.createQuery("from Cases c where c.customer.lastName=:paramLastName");
		getCases.setParameter("paramLastName", lastName);
		return getCases.getResultList();
	}

	@Override
	public List<Cases> getCasesByEmployee(Integer employeeID) {
		Query getCases = entityManager.createQuery("from Cases c where c.employee.id=:paramEmployeeID");
		getCases.setParameter("paramEmployeeID", employeeID);
		return getCases.getResultList();
	}

	@Override
	public List<Cases> getCasesByCustomer(Integer customerID) {
		Query getCases = entityManager.createQuery("from Cases c where c.customer.id=:paramCustomerID");
		getCases.setParameter("paramCustomerID", customerID);
		return getCases.getResultList();
	}

	@Override
	public Cases saveCase(Cases cases) {
		//merge to save new/update old
		Cases dbCustomer = entityManager.merge(cases);
		//set arg id to db id for save/update ops
		cases.setId(dbCustomer.getId());
		return dbCustomer;
	}

	@Override
	public void deleteCaseByID(Integer ID) {
		Query deleteCustomer = entityManager.createQuery("delete from Cases where id=:casesID");
		deleteCustomer.setParameter("casesID", ID);
		deleteCustomer.executeUpdate();
	}

}
