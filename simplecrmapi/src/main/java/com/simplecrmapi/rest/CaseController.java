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
import com.simplecrmapi.service.CasesService;

@RestController
@RequestMapping("/cases")
public class CaseController {

	@Autowired
	private CasesService casesService;
	
	
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
	
	@GetMapping("/id")
	public ResponseEntity<Object> getCaseByID(@RequestParam("id") int ID) {
		return ResponseEntity.ok(casesService.getCaseByID(ID));
	}
	
	@PostMapping("")
	public ResponseEntity<Object> saveNewCase(@Valid @RequestBody Cases cases, @RequestParam("empId") int empID) throws URISyntaxException{
		Cases newCase = casesService.saveNewCase(cases, empID);
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
}
