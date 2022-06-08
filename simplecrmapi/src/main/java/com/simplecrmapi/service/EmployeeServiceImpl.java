package com.simplecrmapi.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.simplecrmapi.dao.AddressDAO;
import com.simplecrmapi.dao.CasesDAO;
import com.simplecrmapi.dao.EmployeeDAO;
import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.util.CustomerInvalidAddressException;
import com.simplecrmapi.util.CustomerInvalidSocialMediaException;
import com.simplecrmapi.util.EntityNotFound;
import com.simplecrmapi.util.InvalidParamsException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeDAO employeeDAO;
	
	@Autowired
	private AddressDAO addressDAO;
	
	@Autowired
	private CasesDAO casesDAO;
	
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
		//Trade cumbersome set iteration for 2 queries and comparator?
		try {
			Employee emp = employeeDAO.getEmployeeByID(empID);
			Cases cases = casesDAO.getCaseByID(caseID);
			//Check if cases is part of emp set
			if(emp.getCases().contains(cases)) {
				return cases.getCustomer();
			}else {
				return null;
			}
		}catch(Exception e) {
			throw new EntityNotFound();
		}
	}

	
	@Override
	@Transactional
	public Employee saveEmployeeDetails(Employee employee) {
		if(employee.getId()==null) {
			throw new EntityNotFound();
		}else if((employee.getAddress().isEmpty()||employee.getAddress().equals(null))&&(employee.getSocialMedia()==null||!employee.getSocialMedia().getPreferredSocialMedia().equals("NO_PREFERENCE"))) {
			
		}else if(employee.getAddress().isEmpty()||employee.getAddress().equals(null)) {
			throw new CustomerInvalidAddressException();
		}else if(employee.getSocialMedia()==null||!employee.getSocialMedia().getPreferredSocialMedia().equals("NO_PREFERENCE")) {
			throw new CustomerInvalidSocialMediaException();
		}
		
		employee.setId(0);
		Employee newEmployee = employeeDAO.saveEmployee(employee);

		if(employee.getAddress().isEmpty()||employee.getAddress()==null) {
			return newEmployee; //TODO customer should be modified if needed			
		}else {
			for(Address add:employee.getAddress()) {
				add.setEmployee(newEmployee); //It works shutup
				addressDAO.save(add);
			}
			return newEmployee;
		}
	}
	
	@Override
	@Transactional
	public Address saveNewAddressToEmployee(Address address, Integer ID) {
		Employee addAddress = getEmployeeByID(ID);
		address.setId(0);
		addAddress.getAddress().add(address);
		
		employeeDAO.saveEmployee(addAddress);
		addressDAO.save(address);
		return address;
	}
	
	@Override
	@Transactional
	public Cases saveNewCaseToEmployee(Cases cases, Integer ID) {
		Employee addCase = getEmployeeByID(ID);
		cases.setId(0);
		addCase.getCases().add(cases);
		
		employeeDAO.saveEmployee(addCase);
		return cases;
	}

	@Override
	@Transactional
	public Address updateEmployeeAddressByID(Address address, Integer ID) {
		return addressDAO.save(address);
	}

	@Override
	public Cases updateEmployeeAssignedCaseByID(Cases cases, Integer ID) {
		Employee addCase = getEmployeeByID(ID);
		addCase.getCases().add(cases);
		
		employeeDAO.saveEmployee(addCase);
		return cases;
	}

	@Override
	public SocialMedia updateEmployeeSocialMedia(@Valid SocialMedia socialMedia, Integer ID) {
		try {
			Employee getEmployeeFromID = getEmployeeByID(ID);
			getEmployeeFromID.setSocialMedia(socialMedia);
			getEmployeeFromID = employeeDAO.saveEmployee(getEmployeeFromID);
			return getEmployeeFromID.getSocialMedia();
		}catch(NullPointerException e){
			return null;
		}
	}

	@Override
	@Transactional
	public void deleteEmployeeByID(Integer ID) {
		employeeDAO.deleteEmployee(ID);
	}

	@Override
	public void deleteEmployeeAddressByIDs(int employeeID, int addressID) {
		employeeDAO.deleteEmployeeAddressByIDs(employeeID, addressID);
	}

	@Override
	public void removeEmployeeAssignedCase(int employeeID, int caseID) {
		Employee emp = getEmployeeByID(employeeID);
		Cases foundCase = new Cases();
		
		//temp
		for(Cases cases: emp.getCases()) {
			if(cases.getId()==caseID) {
				foundCase = cases;
				emp.getCases().remove(cases);
				break;
			}
		}
		//save de-association for employee
		employeeDAO.saveEmployee(emp);
		//save de-association for case
		
		//temp
		for(Employee emps:foundCase.getEmployee()) {
			if(emps.getId()==employeeID) {
				foundCase.getEmployee().remove(emps);
				break;
			}
		}
		casesDAO.saveCase(foundCase);
	}

	
}
