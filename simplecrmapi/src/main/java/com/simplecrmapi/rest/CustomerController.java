package com.simplecrmapi.rest;

import java.net.URI;
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

import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.entity.User;
import com.simplecrmapi.service.CustomerService;
import com.simplecrmapi.service.EmployeeService;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@GetMapping("")
	public ResponseEntity<List<Customer>> getCustomers(){
		return ResponseEntity.ok(customerService.getCustomers());
	}
	
	@GetMapping("/id")
	public ResponseEntity<Customer> getCustomerByID(@RequestParam(name="id") int ID) {
		Customer retrieved = customerService.getCustomerByID(ID);
		return retrieved==null?ResponseEntity.notFound().build():ResponseEntity.ok(retrieved);
	}
	
	//LIST
	@GetMapping("/lastname")
	public ResponseEntity<Object> getCustomersByLastName(@RequestParam(name="lastname") String lastName){
		List<Customer> lastNames = customerService.getCustomerByLastName(lastName);
		
		return lastNames.isEmpty()||lastNames==null?ResponseEntity.notFound().build():ResponseEntity.ok(lastNames);
	}
	//LIST
	@GetMapping("/firstname")
	public ResponseEntity<Object> getCustomersByFirstName(@RequestParam(name="firstname") String firstName){
		List<Customer> firstNames = customerService.getCustomerByFirstName(firstName);
		
		return firstNames.isEmpty()||firstNames==null?ResponseEntity.notFound().build():ResponseEntity.ok(firstNames);
	}
	
	@GetMapping("/users")
	public ResponseEntity<Object> getUserCustomersFromCases(){
		return ResponseEntity.ok(customerService.getUserCustomersFromCases(getEmployeeFromSession()));
	}
	
	//Post/Put changed to response entity w/ bodies, more api like and works with testing
	@PostMapping("")
	public ResponseEntity<Customer> saveNewCustomerDetails(@Valid @RequestBody Customer customer) throws Exception {
		customer.setId(0); //here because service layer shared with PUT
		Customer newCustomer = customerService.saveCustomerDetails(customer);

		return ResponseEntity.created(new URI("/customers/id/"+newCustomer.getId())).body(newCustomer);
	}
	
	@PostMapping("/id/addresses")
	public ResponseEntity<Object> saveNewAddressToCustomer(@Valid @RequestBody Address address, @RequestParam(name="id") int ID) throws Exception{
		Address newAddress = customerService.saveAddressToCustomer(address, ID);
		return ResponseEntity.created(new URI("/customers/id/addresses/"+newAddress.getId())).body(newAddress);
	}
	
	@PutMapping("")
	public ResponseEntity<Customer> saveCustomerDetails(@Valid @RequestBody Customer customer) throws Exception{
		Customer updatedCustomer = customerService.saveCustomerDetails(customer);
		return ResponseEntity.ok(updatedCustomer);
	}
	
	//TODO temp ifs
	//TODO validation catch, null catch
	@PutMapping("/id/socialmedia")
	public ResponseEntity<Object> updateCustomerSocialMedia(@Valid @RequestBody SocialMedia socialMedia, @RequestParam(name="id") int ID){
		SocialMedia sm = customerService.updateCustomerSocialMedia(socialMedia, ID);
		if(sm==null) {
			return ResponseEntity.badRequest().body("400 BAD REQUEST: Unable to update customer's social media");
		}
		return ResponseEntity.ok(sm);
	}
	
	@PutMapping("/id/addresses")
	public ResponseEntity<Object> updateCustomerAddressByID(@Valid @RequestBody Address address){
		Address updatedAddress = customerService.updateCustomerAddressByID(address);
		if(updatedAddress==null) {
			return ResponseEntity.badRequest().body("400 BAD REQUEST: Unable to update customer's address");
		}
		return ResponseEntity.ok(updatedAddress);
	}
	
	@PutMapping("/users")
	public ResponseEntity<Object> updateUserAssignedCustomerByID(@Valid @RequestBody Customer customer){
		Customer updatedCus = customerService.updateUserCustomerByID(customer, getEmployeeFromSession());
		return updatedCus==null?ResponseEntity.notFound().build():ResponseEntity.ok(updatedCus);
	}
	
	@PutMapping("/users/addresses")
	public ResponseEntity<Object> updateUserAssignedCustomerAddressByID(@Valid @RequestBody Address address){
		Address updatedAddress = customerService.updateUserAssignedAdressByID(address, getEmployeeFromSession());
		return updatedAddress==null?ResponseEntity.notFound().build():ResponseEntity.ok(updatedAddress);
	}
	
	@PutMapping("/users/socialmedia")
	public ResponseEntity<Object> updateUserAssignedCustomerSocialMediaByID(@Valid @RequestBody SocialMedia socialMedia){
		SocialMedia updatedMedia = customerService.updateUserAssignedSocialMediaByID(socialMedia, getEmployeeFromSession());
		return updatedMedia==null?ResponseEntity.notFound().build():ResponseEntity.ok(updatedMedia);
	}
	
	//204 for now, but who knows
	@DeleteMapping("id")
	public ResponseEntity<Object> deleteCustomerByID(@RequestParam(name="id") int ID) {
		customerService.deleteCustomerByID(ID);
		return ResponseEntity.noContent().build();
	}
	
	//Dodgy as fuck, worth revisiting
	@DeleteMapping("/id/socialmedia")
	public ResponseEntity<Object> deleteCustomerSocialMedia(@RequestParam(name="id") int ID){
		customerService.deleteCustomerSocialMediaByID(ID);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/id/addresses")
	public ResponseEntity<Object> deleteCustomerAddressByIDs(@RequestParam(name="customerid") int customerID, @RequestParam(name="addressid") int addressID){
		customerService.deleteCustomerAddressByID(customerID, addressID);
		return ResponseEntity.noContent().build();
	}
	
	private Employee getEmployeeFromSession() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return employeeService.getEmployeeByID(user.getEmployeeID());
	}
	
	//TODO Advanced GET mappings - Maybe be moved to other controllers
	
	//Retrieve customers by service/product(s)
	//@GetMapping("/customers/products") and @RequestParam(name="product")
	
	//TODO Advanced DELETE mappings - may be moved to other controllers
	
	//Delete all of x product from customers (i.e product discontinuation/removal)
	//@DeleteMapping("/customers/products/") and @RequestParam(name="product")
}