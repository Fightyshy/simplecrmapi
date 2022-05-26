package com.simplecrmapi.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.service.EmployeeService;
import com.simplecrmapi.util.EntityNotFound;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@GetMapping("")
	public ResponseEntity<List<Employee>> getEmployees(){
		return ResponseEntity.ok(employeeService.getEmployees());
	}
	
	@GetMapping("/id")
	public ResponseEntity<Object> getEmployeeByID(@RequestParam(name="id") int ID) {
		Employee emp = employeeService.getEmployeeByID(ID);
		
		if(emp!=null) {
			return ResponseEntity.ok(employeeService.getEmployeeByID(ID));
		}else {
			throw new EntityNotFound(); //temp
//			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("404 NOT FOUND: Entity not found with parameters"); //Is ideal, not working atm
		}
	}
	
	@GetMapping("/id/cases")
	public ResponseEntity<Object> getEmployeeCasesByID(@RequestParam(name="id") int ID){
		List<Cases> cases = employeeService.getEmployeeCasesByID(ID);
		return ResponseEntity.ok(cases);
	}
	
	@GetMapping("/id/customers")
	public ResponseEntity<Object> getCustomersAssignedToEmployee(@RequestParam(name="id") int ID){
		List<Customer> customers = employeeService.getCustomersAssignedToEmployee(ID);
		return ResponseEntity.ok(customers);
	}
	
	@GetMapping("/id/cases/customer")
	public ResponseEntity<Object> getCustomerFromEmployeeAssignedCase(@RequestParam(name="empId") int empID, @RequestParam(name="caseId") int caseID){
		Customer customer = employeeService.getCustomerFromEmployeeAssignedCase(empID, caseID);
		return ResponseEntity.ok(customer);
	}
	
	@PostMapping("")
	public ResponseEntity<Object> saveEmployee(@Valid @RequestBody Employee employee) throws Exception {
		employee.setId(0);
		Employee newEmployee = employeeService.saveEmployeeDetails(employee);
		return ResponseEntity.created(new URI("/employees/id/"+newEmployee.getId())).body(newEmployee);
	}
	
	@PutMapping("")
	public ResponseEntity<Object> updateEmployee(@Valid @RequestBody Employee employee) throws URISyntaxException {
		Employee updatedEmployee = employeeService.saveEmployeeDetails(employee);
		return ResponseEntity.created(new URI("/employees/id/"+updatedEmployee.getId())).body(updatedEmployee);
	}
	
	@DeleteMapping("/id")
	public ResponseEntity<Object> deleteEmployeeByID(@RequestParam(name="id") int ID) {
		employeeService.deleteEmployeeByID(ID);
		return ResponseEntity.noContent().build();
	}
}
