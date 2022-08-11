package com.simplecrmapi.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplecrmapi.dao.CasesDAO;
import com.simplecrmapi.dao.EmployeeDAO;
import com.simplecrmapi.dao.ProductsDAO;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.util.IDComparator;

@Service
public class CasesServiceImpl implements CasesService {
	
	@Autowired
	private CasesDAO casesDAO;
	
	@Autowired
	private EmployeeDAO employeeDAO;
	
	@Autowired
	private ProductsDAO productDAO;
	
	private IDComparator comparator;

	@Override
	@Transactional
	public List<Cases> getAllCases() {
		return casesDAO.getCases();
	}

	@Override
	@Transactional
	public List<Cases> getCasesByLastName(String lastName) {
		return casesDAO.getCasesByCustomerLastName(lastName);
	}

	@Override
	@Transactional
	public List<Cases> getCasesByFirstName(String firstName) {
		return casesDAO.getCasesByCustomerFirstName(firstName);
	}

	@Override
	@Transactional
	public Cases getCaseByID(Integer ID) {
		return casesDAO.getCaseByID(ID);
	}

	@Override
	@Transactional
	public Cases saveNewCase(Cases cases, String product, Integer empID) {
		cases.setId(0);
		
		Employee creator = employeeDAO.findById(empID).orElseGet(null);
		HashSet<Employee> emps = new HashSet<Employee>();
		emps.add(creator);
		cases.setEmployee(emps);
		cases.setProduct(productDAO.findByName(product));
		creator.getCases().add(cases);
		Cases newCase = casesDAO.saveCase(cases);
		employeeDAO.saveAndFlush(creator);
		
		return newCase;
	}
	//SaveEmployeeToCase

	//Does not update relationships
	@Override
	@Transactional
	public Cases updateCase(Cases cases) {
		Cases updatedCase = casesDAO.saveCase(cases);
		return updatedCase;
	}
	
	@Override
	@Transactional
	public Cases updateCase(Cases cases, Employee employee) {
		for(Cases empCase: employee.getCases()) {
			if(comparator.EqualsID(cases, empCase)) {
				return casesDAO.saveCase(cases);
			}
		}
		return null;
	}
	
	@Override
	@Transactional
	public Cases saveEmployeeToCase(Cases cases, Integer empID) {
		Employee toAdd = employeeDAO.findById(empID).orElseGet(null);
		toAdd.getCases().add(cases);
		employeeDAO.saveAndFlush(toAdd);
		
		cases.getEmployee().add(toAdd);
		return casesDAO.saveCase(cases);
	}
	
	@Override
	@Transactional
	public void deleteCaseByID(Integer ID) {
		try {
			casesDAO.deleteCaseByID(ID);
		}catch(Exception e) {
			
		}
	}

	
	@Override
	@Transactional
	public void deleteEmployeeFromCase(Integer ID, Integer empID) {
		try {
			//temp loops
			Cases temp = casesDAO.getCaseByID(ID);
			Employee tempEmp = employeeDAO.findById(empID).orElseGet(null);
			
			temp.getEmployee().remove(tempEmp);
			tempEmp.getCases().remove(temp);
			
			casesDAO.saveCase(temp);
			employeeDAO.saveAndFlush(tempEmp);
		}catch(Exception e) {
			
		}
	}

	@Override
	public List<Customer> getCustomersFromCaseProducts(String product) {
		List<Cases> cases = casesDAO.getCases();
		List<Customer> cus = new ArrayList<>();
		
		for(Cases cased: cases) {
			if(cased.getProduct().getName().equals(product)) {
				cus.add(cased.getCustomer());
			}
		}
		
		return cus.isEmpty()?null:cus;
		
	}

	@Override
	public void deleteCasesWithDiscontinuedProducts(String product) {
		casesDAO.deleteCaseByProducts(product);
	}
}
