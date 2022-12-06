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

	private EmployeeDAO employeeDAO;
	private AddressDAO addressDAO;
	private CasesDAO casesDAO;
	
	private IDComparator comparator;
	
	public EmployeeServiceImpl(EmployeeDAO employeeDAO, AddressDAO addressDAO, CasesDAO casesDAO) {
		this.employeeDAO = employeeDAO;
		this.addressDAO = addressDAO;
		this.casesDAO = casesDAO;
	}

	@Override
	@Transactional
	public List<Employee> getEmployees() {
		return employeeDAO.findAll();
	}

	@Override
	@Transactional
	public Employee getEmployeeByID(Integer ID) {
		try {
			return employeeDAO.findById(ID).orElseGet(null);
		}catch(Exception e) {
			return null;
		}
	}
	
	@Override
	@Transactional
	public List<Cases> getEmployeeCasesByID(int ID) {
		try {
			Employee emp = employeeDAO.findById(ID).orElseGet(null);
			List<Cases> cases = new ArrayList<Cases>(emp.getCases());
			return cases;
		}catch(Exception e) {
			return null;
		}
	}

	
	@Override
	@Transactional
	public List<Customer> getCustomersAssignedToEmployee(int ID) {
		Employee emp = employeeDAO.findById(ID).orElseGet(null);
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
//			Customer cus = casesDAO.findCustomerFromEmployeeAssignedCase(empID, caseID);
//			return cus==null?null:cus;
			
			Employee emp = employeeDAO.findById(empID).orElseGet(null);
			for(Cases cases:emp.getCases()) {
				if(cases.getId()==caseID&&cases.getEmployee().contains(emp)) {
					return cases.getCustomer();
				}
			}
			return null;
			//			Employee emp = employeeDAO.findById(empID).orElseGet(null);
//			Cases cases = casesDAO.findById(caseID).orElseGet(null);
//			//Check if cases is part of emp set
//			if(emp.getCases().contains(cases)) {
//				return cases.getCustomer();
//			}else {
//				return null;
//			}
		}catch(Exception e) {
			throw new EntityNotFound();
		}
	}
	
	//TODO
	@Override
	@Transactional
	public Customer getCustomerFromEmployeeAssignedCase(Employee emp, int caseID) {
		try {
			Cases cases = casesDAO.findById(caseID).orElseGet(null);
			return cases==null?null:cases.getCustomer();
		}catch(Exception e) {
			throw new EntityNotFound();
		}
	}

	@Override
	@Transactional
	public Employee getEmployeeByEmailAddress(String email) {
		try {
			return employeeDAO.findByEmailAddress(email);
		}catch(Exception e) {
			return null;
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
		Employee newEmployee = employeeDAO.saveAndFlush(employee);

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
		
		employeeDAO.saveAndFlush(emp);
		Address add = addressDAO.save(address);
		return add;
	}
	
	@Override
	@Transactional
	public Address saveNewAddressToEmployee(Address address, Employee emp) {
		address.setId(0);
		emp.getAddress().add(address);
		
		employeeDAO.saveAndFlush(emp);
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
		Cases cased = casesDAO.saveAndFlush(cases);
		
		emp.getCases().add(cased);
		employeeDAO.saveAndFlush(emp);
		return cased;
	}
	
	@Override
	@Transactional
	public Cases saveNewCaseToEmployee(Cases cases, Employee emp) {
		cases.setId(0);
		
		cases.setEmployee(new HashSet<Employee>());
		cases.getEmployee().add(emp);
		Cases cased = casesDAO.saveAndFlush(cases);
		
		emp.getCases().add(cased);
		employeeDAO.saveAndFlush(emp);
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
		Cases cased = casesDAO.saveAndFlush(cases);
		
		emp.getCases().add(cased);
		employeeDAO.saveAndFlush(emp);
		return cased;
	}
	
	@Override
	@Transactional
	public Cases updateEmployeeAssignedCaseByID(Cases cases, Employee emp) {
		
		for(Cases cased:emp.getCases()) {
			if(comparator.EqualsID(cased, cases)) {
				cases.setEmployee(new HashSet<Employee>());
				cases.getEmployee().add(emp);
				Cases caser = casesDAO.saveAndFlush(cases);
				emp.getCases().add(caser);
				employeeDAO.saveAndFlush(emp);
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
			emp = employeeDAO.saveAndFlush(emp);
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
			emp = employeeDAO.saveAndFlush(emp);
			return emp.getSocialMedia();
		}
		return null;
	}

	@Override
	@Transactional
	public void deleteEmployeeByID(Integer ID) {
		employeeDAO.deleteById(ID);
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
		employeeDAO.saveAndFlush(emp);
		//save de-association for case
		
		//temp
		for(Employee emps:foundCase.getEmployee()) {
			if(emps.getId()==employeeID) {
				foundCase.getEmployee().remove(emps);
				break;
			}
		}
		casesDAO.saveAndFlush(foundCase);
	}

	
}
