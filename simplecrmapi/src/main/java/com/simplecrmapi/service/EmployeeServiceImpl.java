package com.simplecrmapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplecrmapi.dao.AddressDAO;
import com.simplecrmapi.dao.EmployeeDAO;
import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.util.CustomerInvalidAddressException;
import com.simplecrmapi.util.CustomerInvalidSocialMediaException;
import com.simplecrmapi.util.EntityNotFound;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeDAO employeeDAO;
	
	@Autowired
	private AddressDAO addressDAO;
	
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
		
		emp.getCases().stream().forEach(cases->{
			if(!customers.contains(cases.getCustomer())) {
				customers.add(cases.getCustomer());
			}
		});
		
		return customers;
	}
	
	@Override
	public Customer getCustomerFromEmployeeAssignedCase(int empID, int caseID) {
		Employee emp = employeeDAO.getEmployeeByID(empID);
		Cases cases = new Cases();
		//cleaner way doko
		for(Cases cas:emp.getCases()) {
			if(cas.getId()==caseID) {
				cases = cas;
				break;
			}
		}
		return cases.getCustomer();
	}

	
	@Override
	@Transactional
	public Employee saveEmployeeDetails(Employee employee) {
		Employee newEmployee;
		if(employee.getId()==null) {
			throw new EntityNotFound();
		}else if((employee.getAddress().isEmpty()||employee.getAddress().equals(null))&&(employee.getSocialMedia()==null||!employee.getSocialMedia().getPreferredSocialMedia().equals("NO_PREFERENCE"))) {
			
		}else if(employee.getAddress().isEmpty()||employee.getAddress().equals(null)) {
			throw new CustomerInvalidAddressException();
		}else if(employee.getSocialMedia()==null||!employee.getSocialMedia().getPreferredSocialMedia().equals("NO_PREFERENCE")) {
			throw new CustomerInvalidSocialMediaException();
		}
		
		if(employee.getAddress().isEmpty()||employee.getAddress()==null) {
			newEmployee = employeeDAO.saveEmployee(employee);
			return newEmployee; //TODO customer should be modified if needed			
		}else {
			newEmployee = employeeDAO.saveEmployee(employee);
			for(Address add:employee.getAddress()) {
				add.setEmployee(newEmployee); //It works shutup
				addressDAO.save(add);
			}
			return newEmployee;
		}
	}

	@Override
	@Transactional
	public void deleteEmployeeByID(Integer ID) {
		employeeDAO.deleteEmployee(ID);
	}




}
