package com.simplecrmapi.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplecrmapi.dao.CasesDAO;
import com.simplecrmapi.dao.EmployeeDAO;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.util.EntityNotFound;

@Service
public class CasesServiceImpl implements CasesService {
	
	@Autowired
	private CasesDAO casesDAO;
	
	@Autowired
	private EmployeeDAO employeeDAO;

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
	public Cases saveNewCase(Cases cases, Integer empID) {
		cases.setId(0);
		
		Employee creator = employeeDAO.getEmployeeByID(empID);
		HashSet<Employee> emps = new HashSet<Employee>();
		emps.add(creator);
		cases.setEmployee(emps);
		creator.getCases().add(cases);
		Cases newCase = casesDAO.saveCase(cases);
		employeeDAO.saveEmployee(creator);
		
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
			if(empCase.equalsID(cases)) {
				return casesDAO.saveCase(cases);
			}
		}
		throw new EntityNotFound();
	}
	
	@Override
	@Transactional
	public Cases saveEmployeeToCase(Cases cases, Integer empID) {
		Employee toAdd = employeeDAO.getEmployeeByID(empID);
		toAdd.getCases().add(cases);
		employeeDAO.saveEmployee(toAdd);
		
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
			Employee tempEmp = employeeDAO.getEmployeeByID(empID);
			
			temp.getEmployee().remove(tempEmp);
			tempEmp.getCases().remove(temp);
			
			casesDAO.saveCase(temp);
			employeeDAO.saveEmployee(tempEmp);
		}catch(Exception e) {
			
		}
	}
}
