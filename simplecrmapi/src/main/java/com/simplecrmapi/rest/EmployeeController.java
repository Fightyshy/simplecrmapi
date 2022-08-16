package com.simplecrmapi.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.entity.User;
import com.simplecrmapi.service.EmployeeService;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@GetMapping("")
	public ResponseEntity<List<Employee>> getEmployees(){
		List<Employee> retrieved = employeeService.getEmployees();
		
		return retrieved.isEmpty()==false?ResponseEntity.ok(retrieved):ResponseEntity.ok(new ArrayList<>());
	}
	
	@GetMapping("/id")
	@Validated
	public ResponseEntity<Object> getEmployeeByID(@RequestParam("id") @NotNull int ID) {
		Employee retrieved = employeeService.getEmployeeByID(ID);
		
		return retrieved==null?ResponseEntity.notFound().build():ResponseEntity.ok(retrieved);
	}
	
	@GetMapping("/id/cases")
	public ResponseEntity<Object> getEmployeeCasesByID(@RequestParam("id") int ID){
		List<Cases> retrieved = employeeService.getEmployeeCasesByID(ID);
		return retrieved.isEmpty()==false?ResponseEntity.ok(retrieved):ResponseEntity.ok(new ArrayList<>());
	}
	
	@GetMapping("/id/customers")
	public ResponseEntity<Object> getCustomersAssignedToEmployee(@RequestParam("id") int ID){
		List<Customer> retrieved = employeeService.getCustomersAssignedToEmployee(ID);
		return retrieved==null?ResponseEntity.notFound().build():ResponseEntity.ok(retrieved);
	}
	
	@GetMapping("/id/cases/customer")
	public ResponseEntity<Object> getCustomerFromEmployeeAssignedCase(@RequestParam("empId") int empID, @RequestParam("caseId") int caseID){
		Customer retrieved = employeeService.getCustomerFromEmployeeAssignedCase(empID, caseID);
		return retrieved==null?ResponseEntity.notFound().build():ResponseEntity.ok(retrieved);
	}
	
	//Principal-based GET
	@GetMapping("/users")
	public ResponseEntity<Object> getEmployeeByUserSession(){
		return ResponseEntity.ok(getEmployeeFromSession());
	}
	
	@GetMapping("/users/cases")
	public ResponseEntity<Object> getCasesAssignedToUserEmployee(){
		return ResponseEntity.ok(getEmployeeFromSession().getCases());
	}
	@GetMapping("/users/customers")
	public ResponseEntity<Object> getCustomersAssignedToUserEmployee(){
		List<Customer> retrieved = employeeService.getCustomersAssignedToEmployee(getEmployeeFromSession());
		return retrieved.isEmpty()==false?ResponseEntity.ok(retrieved):ResponseEntity.notFound().build();
	}
	
	@GetMapping("/users/cases/customer")
	public ResponseEntity<Object> getCustomerAssignedToUserEmployeeCase(@RequestParam("caseId") int caseID){
		Customer cus = employeeService.getCustomerFromEmployeeAssignedCase(getEmployeeFromSession(), caseID);
		return ResponseEntity.ok(cus);
	}
	
	@PostMapping("")
	public ResponseEntity<Object> saveNewEmployee(@Valid @RequestBody Employee employee) throws Exception {
		employee.setId(0);
		Employee newEmployee = employeeService.saveEmployeeDetails(employee);
		return ResponseEntity.created(new URI("/employees/id/"+newEmployee.getId())).body(newEmployee);
	}
	
	@PostMapping("/id/addresses")
	public ResponseEntity<Object> saveNewAddressToEmployee(@Valid @RequestBody Address address, @RequestParam("id") int ID) throws URISyntaxException{
		Address newAddress = employeeService.saveNewAddressToEmployee(address, ID);
		return ResponseEntity.created(new URI("/employees/id/addresses/"+newAddress.getId())).body(newAddress);
	}
	
	@PostMapping("/id/cases")
	public ResponseEntity<Object> saveNewCaseToEmployee(@Valid @RequestBody Cases cases, @RequestParam("id") int ID) throws URISyntaxException{
		Cases newCase = employeeService.saveNewCaseToEmployee(cases, ID);
		return ResponseEntity.created(new URI("/employees/id/cases/"+newCase.getId())).body(newCase);
	}
	
	//Principal-based POST
	@PostMapping("/users/addresses")
	public ResponseEntity<Object> saveNewAddressToUserEmployee(@Valid @RequestBody Address address) throws URISyntaxException{
		Address newAddress = employeeService.saveNewAddressToEmployee(address, getEmployeeFromSession());
		return ResponseEntity.created(new URI("/employees/id/addresses/"+newAddress.getId())).body(newAddress);
	}
	
	@PostMapping("/users/cases")
	public ResponseEntity<Object>saveNewCaseToUserEmployee(@Valid @RequestBody Cases cases) throws URISyntaxException{
		Cases newCase = employeeService.saveNewCaseToEmployee(cases, getEmployeeFromSession());
		return ResponseEntity.created(new URI("/employees/id/cases"+newCase.getId())).body(newCase);
	}
	
	@PutMapping("")
	public ResponseEntity<Object> updateFullEmployee(@Valid @RequestBody Employee employee) throws URISyntaxException {
		Employee updatedEmployee = employeeService.saveEmployeeDetails(employee);
		return ResponseEntity.ok(updatedEmployee);
	}
	
	//POST
	
	@PutMapping("/id/addresses")
	public ResponseEntity<Object> updateEmployeeAddressByID(@Valid @RequestBody Address address, @RequestParam("id") int ID){
		Address updatedAddress = employeeService.updateEmployeeAddressByID(address, ID);
		return ResponseEntity.ok(updatedAddress);
	}
	
	@PutMapping("/id/cases")
	public ResponseEntity<Object> updateEmployeeAssignedCaseByID(@Valid @RequestBody Cases cases, @RequestParam("id") int ID){
		Cases updatedCase = employeeService.updateEmployeeAssignedCaseByID(cases, ID);
		return ResponseEntity.ok(updatedCase);
	}
	
	@PutMapping("/id/socialmedia")
	public ResponseEntity<Object> updateEmployeeSocialMedia(@Valid @RequestBody SocialMedia socialMedia, @RequestParam("id") int ID){
		SocialMedia updatedSocialMedia = employeeService.updateEmployeeSocialMedia(socialMedia, ID);
		return ResponseEntity.ok(updatedSocialMedia);
	}
	
	//Principal-based PUT
	@PutMapping("/users/addresses")
	public ResponseEntity<Object> updateUserEmployeeAddress(@Valid @RequestBody Address address){
		Address updatedAddress = employeeService.updateEmployeeAddressByID(address, getEmployeeFromSession());
		return updatedAddress==null?ResponseEntity.notFound().build():ResponseEntity.ok(updatedAddress);
	}
	
	@PutMapping("/users/cases")
	public ResponseEntity<Object> updateUserEmployeeAssignedCase(@Valid @RequestBody Cases cases){
		Cases updatedCase = employeeService.updateEmployeeAssignedCaseByID(cases, getEmployeeFromSession());
		return updatedCase==null?ResponseEntity.notFound().build():ResponseEntity.ok(updatedCase);
	}
	
	@PutMapping("/users/socialmedia")
	public ResponseEntity<Object> updateUserEmployeeSocialMedia(@Valid @RequestBody SocialMedia socialMedia){
		SocialMedia updatedSocialMedia = employeeService.updateEmployeeSocialMedia(socialMedia, getEmployeeFromSession());
		return updatedSocialMedia==null?ResponseEntity.notFound().build():ResponseEntity.ok(updatedSocialMedia);
	}
	
	@DeleteMapping("/id")
	public ResponseEntity<Object> deleteEmployeeByID(@RequestParam("id") int ID) {
		employeeService.deleteEmployeeByID(ID);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/id/addresses")
	public ResponseEntity<Object> deleteEmployeeAddressByIDs(@RequestParam("employeeId") int employeeID, @RequestParam("addressId") int addressID){
		employeeService.deleteEmployeeAddressByIDs(employeeID, addressID);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/id/cases")
	public ResponseEntity<Object> removeEmployeeAssignedCase(@RequestParam("employeeId") int employeeID, @RequestParam("caseId") int caseID){
		employeeService.removeEmployeeAssignedCase(employeeID, caseID);
		return ResponseEntity.noContent().build();
	}
	
	//Principal-based DELETE
	@DeleteMapping("/users/addresses")
	public ResponseEntity<Object> deleteUserEmployeeAddress(@RequestParam("addressId") int addressID){
		employeeService.deleteEmployeeAddressByIDs(getEmployeeFromSession(), addressID);
		return ResponseEntity.noContent().build();
	}
	
	private Employee getEmployeeFromSession() {
		User user = SecurityContextHolder.getContext().getAuthentication().isAuthenticated()==true?(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal():null;
		return employeeService.getEmployeeByID(user.getEmployeeID());
	}
}
