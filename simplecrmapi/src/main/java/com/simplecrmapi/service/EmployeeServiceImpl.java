package com.simplecrmapi.service;

import java.util.ArrayList;
import java.util.HashSet;
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
import com.simplecrmapi.util.IDComparator;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeDAO employeeDAO;
	
	@Autowired
	private AddressDAO addressDAO;
	
	@Autowired
	private CasesDAO casesDAO;
	
	private IDComparator comparator;
	
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
	@Transactional
	public List<Customer> getCustomersAssignedToEmployee(Employee emp){
		List<Customer> customers = new ArrayList<Customer>();
		
		emp.getCases().stream().forEach(cases->{
			if(!customers.contains(cases.getCustomer())) {
				customers.add(cases.getCustomer());
			}
		});
		
		return customers;
	}
	
	@Override
	@Transactional
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
	public Customer getCustomerFromEmployeeAssignedCase(Employee emp, int caseID) {
		try {
			Cases cases = casesDAO.getCaseByID(caseID);
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
		Employee emp = getEmployeeByID(ID);
		address.setId(0);
		emp.getAddress().add(address);
		
		employeeDAO.saveEmployee(emp);
		Address add = addressDAO.save(address);
		return add;
	}
	
	@Override
	@Transactional
	public Address saveNewAddressToEmployee(Address address, Employee emp) {
		address.setId(0);
		emp.getAddress().add(address);
		
		employeeDAO.saveEmployee(emp);
		Address add = addressDAO.save(address);
		return add;
	}
	
	@Override
	@Transactional
	public Cases saveNewCaseToEmployee(Cases cases, Integer ID) {
		Employee emp = getEmployeeByID(ID);
		cases.setId(0);
		
		cases.setEmployee(new HashSet<Employee>());
		cases.getEmployee().add(emp);
		Cases cased = casesDAO.saveCase(cases);
		
		emp.getCases().add(cased);
		employeeDAO.saveEmployee(emp);
		return cased;
	}
	
	@Override
	@Transactional
	public Cases saveNewCaseToEmployee(Cases cases, Employee emp) {
		cases.setId(0);
		
		cases.setEmployee(new HashSet<Employee>());
		cases.getEmployee().add(emp);
		Cases cased = casesDAO.saveCase(cases);
		
		emp.getCases().add(cased);
		employeeDAO.saveEmployee(emp);
		return cased;
	}

	@Override
	@Transactional
	public Address updateEmployeeAddressByID(Address address, Integer ID) {
		return addressDAO.save(address);
	}
	
	@Override
	@Transactional
	public Address updateEmployeeAddressByID(Address address, Employee emp) {
		for(Address add: emp.getAddress()) {
			if(comparator.EqualsID(address, add)){
				return addressDAO.save(address);
			}
		}
		return null;
	}

	@Override
	@Transactional
	public Cases updateEmployeeAssignedCaseByID(Cases cases, Integer ID) {
		Employee emp = getEmployeeByID(ID);
		cases.setEmployee(new HashSet<Employee>());
		cases.getEmployee().add(emp);
		Cases cased = casesDAO.saveCase(cases);
		
		emp.getCases().add(cased);
		employeeDAO.saveEmployee(emp);
		return cased;
	}
	
	@Override
	@Transactional
	public Cases updateEmployeeAssignedCaseByID(Cases cases, Employee emp) {
		
		for(Cases cased:emp.getCases()) {
			if(comparator.EqualsID(cased, cases)) {
				cases.setEmployee(new HashSet<Employee>());
				cases.getEmployee().add(emp);
				Cases caser = casesDAO.saveCase(cases);
				emp.getCases().add(caser);
				employeeDAO.saveEmployee(emp);
				return caser;
			}
		}
		return null;
	}

	@Override
	@Transactional
	public SocialMedia updateEmployeeSocialMedia(@Valid SocialMedia socialMedia, Integer ID) {
		try {
			Employee emp = getEmployeeByID(ID);
			emp.setSocialMedia(socialMedia);
			emp = employeeDAO.saveEmployee(emp);
			return emp.getSocialMedia();
		}catch(NullPointerException e){
			System.out.println("Null pointer exception: "+e);
			return null;
		}
	}
	
	@Override
	@Transactional
	public SocialMedia updateEmployeeSocialMedia(@Valid SocialMedia socialMedia, Employee emp) {
		if(comparator.EqualsID(socialMedia, emp.getSocialMedia())) {
			emp.setSocialMedia(socialMedia);
			emp = employeeDAO.saveEmployee(emp);
			return emp.getSocialMedia();
		}
		return null;
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
	public void deleteEmployeeAddressByIDs(Employee emp, int addressID) {
		employeeDAO.deleteEmployeeAddressByIDs(emp.getId(), addressID);
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
