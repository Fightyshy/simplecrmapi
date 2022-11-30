package com.simplecrmapi.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.simplecrmapi.entity.User;
import com.simplecrmapi.service.CasesService;
import com.simplecrmapi.service.EmployeeService;

@RestController
@RequestMapping("/cases")
public class CaseController {

	private CasesService casesService;
	private EmployeeService employeeService;
	
	public CaseController(CasesService casesService, EmployeeService employeeService) {
		this.casesService = casesService;
		this.employeeService = employeeService;
	}

	@GetMapping("")
	public ResponseEntity<List<Cases>> getAllCases() {
		return ResponseEntity.ok(casesService.getAllCases());
	}
	
	@GetMapping("/lastname")
	public ResponseEntity<List<Cases>> getCasesByLastName(@RequestParam("lastname") String lastName){
		return ResponseEntity.ok(casesService.getCasesByLastName(lastName));
	}
	
	@GetMapping("/firstname")
	public ResponseEntity<List<Cases>> getCasesByFirstName(@RequestParam("firstname") String firstName){
		return ResponseEntity.ok(casesService.getCasesByFirstName(firstName));
	}
	
	@GetMapping("/products/customers")
	public ResponseEntity<Object> getCustomersByProducts(@RequestParam(name="product") String product){
		List<Customer> products = casesService.getCustomersFromCaseProducts(product);
		return products.isEmpty()?ResponseEntity.notFound().build():ResponseEntity.ok(products);
	}
	
	@GetMapping("/id")
	public ResponseEntity<Object> getCaseByID(@RequestParam("id") int ID) {
		return ResponseEntity.ok(casesService.getCaseByID(ID));
	}
	
	@GetMapping("/users")
	public ResponseEntity<List<Cases>> getAllEmployeeUserCases(){
		return ResponseEntity.ok(new ArrayList<>(getEmployeeFromSession().getCases()));
	}
	
	@PostMapping("")
	public ResponseEntity<Object> saveNewCase(@Valid @RequestBody Cases cases, @RequestParam("productId") int product, @RequestParam("empId") int empID) throws URISyntaxException{
		Cases newCase = casesService.saveNewCase(cases, product,empID);
		return ResponseEntity.created(new URI("/cases/id/"+newCase.getId())).body(newCase);
	}
	
	@PostMapping("/users")
	public ResponseEntity<Object> saveNewCaseToUser(@Valid @RequestBody Cases cases, @RequestParam("productId") int product) throws URISyntaxException{
		Cases newCase = casesService.saveNewCase(cases, product, getEmployeeFromSession().getId());
		return ResponseEntity.created(new URI("/cases/id/"+newCase.getId())).body(newCase);
	}
	
	//PostMapping("id/employees")
	//Save new employee to case
	@PutMapping("/id/employees")
	public ResponseEntity<Object> saveEmployeeToCase(@Valid @RequestBody Cases cases, @RequestParam("empId") int empID){
		Cases updatedCase = casesService.saveEmployeeToCase(cases, empID);
		return ResponseEntity.ok(updatedCase);
	}
	
	//only for updating cases but no relationships
	@PutMapping("")
	public ResponseEntity<Object> updateCase(@Valid @RequestBody Cases cases){
		return ResponseEntity.ok(casesService.updateCase(cases));
	}
	
	@PutMapping("/users")
	public ResponseEntity<Object> updateUserCases(@Valid @RequestBody Cases cases){
		Cases updatedCase = casesService.updateCase(cases, getEmployeeFromSession());
		return updatedCase==null? ResponseEntity.notFound().build():ResponseEntity.ok(updatedCase);
	}
	
	@DeleteMapping("/id")
	public ResponseEntity<Object> deleteCaseByID(@RequestParam("id") int ID){
		casesService.deleteCaseByID(ID);
		return ResponseEntity.noContent().build();
	}
	
	//for removing employee from case
	@DeleteMapping("/id/employees")
	public ResponseEntity<Object> deleteEmployeeFromCase(@RequestParam("id") int ID, @RequestParam("empId") int empID){
		casesService.deleteEmployeeFromCase(ID, empID);
		return ResponseEntity.noContent().build();
	}
	
	//Ideally, this would archive all active cases into a archive repo before deleting
	@DeleteMapping("/products/customers")
	public ResponseEntity<Object> deleteCasesWithDiscontinuedProducts(@RequestParam("productId") Integer productID){
		casesService.deleteCasesWithDiscontinuedProducts(productID);
		return ResponseEntity.noContent().build();
	}

	//Add user style controls
	private Employee getEmployeeFromSession() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return employeeService.getEmployeeByID(user.getEmployee().getId());
	}
	
	//No user delete endpoint, user shouldn't be allowed to delete case without manager consent (archive under status is different)
}
