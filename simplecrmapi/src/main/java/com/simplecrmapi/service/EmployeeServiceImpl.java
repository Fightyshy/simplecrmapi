package com.simplecrmapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplecrmapi.dao.EmployeeDAO;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.util.EntityNotFound;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeDAO employeeDAO;
	
	@Override
	@Transactional
	public List<Employee> getEmployees() {
		return employeeDAO.getEmployees();
	}

	@Override
	@Transactional
	public Employee getEmployeeByID(Integer ID) {
		if(ID==null) {
			throw new EntityNotFound();
		}
		return employeeDAO.getEmployeeByID(ID);
	}
	
	@Override
	@Transactional
	public List<Cases> getEmployeeCasesByID(int ID) {
		Employee emp = employeeDAO.getEmployeeByID(ID);
		List<Cases> cases = new ArrayList<Cases>(emp.getCases());
		
		return cases;
	}

	
	@Override
	@Transactional
	public List<Customer> getCustomersAssignedToEmployee(int ID) {
		Employee emp = employeeDAO.getEmployeeByID(ID);
		List<Customer> customers = new ArrayList<Customer>();
		
		for(Cases cases:emp.getCases()) {
			if(!customers.contains(cases.getCustomer())) {
				customers.add(cases.getCustomer());
			}
		}
		
		return customers;
	}
	
	@Override
	@Transactional
	public Employee saveEmployeeDetails(Employee employee) {
		return employeeDAO.saveEmployee(employee);
	}

	@Override
	@Transactional
	public void deleteEmployeeByID(Integer ID) {
		employeeDAO.deleteEmployee(ID);
	}



}
