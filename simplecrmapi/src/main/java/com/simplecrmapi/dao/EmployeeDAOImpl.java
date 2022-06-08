package com.simplecrmapi.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.util.EntityNotFound;
import com.simplecrmapi.util.InvalidParamsException;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

	private EntityManager entityManager;
	
	public EmployeeDAOImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Override
	public List<Employee> getEmployees() {
//		Query getEmployees = entityManager.createQuery("from Employee", Employee.class);
		Query getEmployees = entityManager.createQuery("select e from Employee e left join fetch e.cases c left join fetch c.customer", Employee.class);
		return getEmployees.getResultList();
	}

	@Override
	public Employee getEmployeeByID(Integer ID) {
		try {
			Employee employee = entityManager.find(Employee.class, ID);
			return employee;
		}catch(IllegalArgumentException e) {
//			e.printStackTrace();
//			System.out.println(">>> An exception due to invalid arguements has occurred, returning null");
			throw new InvalidParamsException();
		}
	}

	@Override
	public Employee saveEmployee(Employee employee) {
		//merge to save new/update old
//		Employee dbCustomer = entityManager.merge(employee);
		//set arg id to db id for save/update ops
//		employee.setId(dbCustomer.getId());
//		return employee;
		Employee dbEmployee;
		try {
			//merge to save new/update old
//			if(employee.getId()==0) {
//				entityManager.persist(employee);
//				entityManager.flush();
//				return employee;
//			}else if(employee.getId()!=0) {
//				dbEmployee = entityManager.merge(employee);
//				return dbEmployee;
//			}
			
			dbEmployee = entityManager.merge(employee);
			return dbEmployee;
			
//			return null;
		}catch(NullPointerException npe) {
			throw new EntityNotFound();
		}catch(EntityNotFoundException enfe) {
			throw new EntityNotFound();
		}
	}

	@Override
	public void deleteEmployee(Integer ID) {
		Query deleteCustomer = entityManager.createQuery("delete from Employee where id=:customerID");
		deleteCustomer.setParameter("customerID", ID);
		deleteCustomer.executeUpdate();
	}

	@Override
	public void deleteEmployeeAddressByIDs(int employeeID, int addressID) {
		Query deleteAddress = entityManager.createQuery("delete from Address a where a.id=:addressID and a.employee.id=:employeeID");
		deleteAddress.setParameter("customerID", employeeID);
		deleteAddress.setParameter("addressID", addressID);
		deleteAddress.executeUpdate();
	}

	
}
