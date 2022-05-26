package com.simplecrmapi.test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecrmapi.dao.EmployeeDAO;
import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.rest.EmployeeController;
import com.simplecrmapi.service.EmployeeService;
import com.simplecrmapi.test.util.CSVParser;
import com.simplecrmapi.util.EntityNotFound;

@WebMvcTest(EmployeeController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class EmployeeControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	private EmployeeService employeeService;
	
	@MockBean
	private EmployeeDAO employeeDAO;
	
	@InjectMocks
	private EmployeeController employeeController;
	
	private List<Employee> employeeTestingSet = new ArrayList<>();
	
//	@BeforeAll //once before all tests
	@BeforeEach //once before every test
	void init() {
		employeeTestingSet = new ArrayList<>(); //clear to reset
		CSVParser parser = new CSVParser();
		List<String[]> employeeFile = parser.readLine("Employee.txt");
		List<Employee> testEmployees = parser.employeeParser(employeeFile);
		
		List<String[]> customerFile = parser.readLine("Customer.txt");
		List<Customer> testCustomers = parser.customerParser(customerFile);
//		System.out.println(testCustomers.toString());
		
		List<String[]> CasesFile = parser.readLine("Cases.txt");
		List<Cases> testCases = parser.caseParser(CasesFile);
		
		List<String[]> addressFile = parser.readLine("Address.txt");
		List<Address> testAddresses = parser.addressParser(addressFile);
//		System.out.println(testAddresses.toString());
		
		List<String[]> socialMedia = parser.readLine("SocialMedia.txt");
		List<SocialMedia> testSocialMedia = parser.socialMediaParser(socialMedia);
//		System.out.println(testSocialMedia.toString());
		
		ArrayList<Address> address1 = new ArrayList<Address>();
		address1.add(testAddresses.get(0));
		
		ArrayList<Address> address2 = new ArrayList<Address>();
		address2.add(testAddresses.get(0));
		address2.add(testAddresses.get(1));
		
		ArrayList<Address> address3 = new ArrayList<Address>();
		address3.add(testAddresses.get(2));
		address3.add(testAddresses.get(3));
		address3.add(testAddresses.get(4));
		
		ArrayList<Address> address4 = new ArrayList<Address>(); //random 4
		address4.add(testAddresses.get(1));
		address4.add(testAddresses.get(4));
		address4.add(testAddresses.get(3));
		address4.add(testAddresses.get(0));
		
		ArrayList<Address> address5 = new ArrayList<Address>();
		address2.add(testAddresses.get(0));
		address2.add(testAddresses.get(1));
		address2.add(testAddresses.get(2));
		address2.add(testAddresses.get(3));
		address2.add(testAddresses.get(4));
		
		Customer customer1 = testCustomers.get(0);
		customer1.setAddress(address1);
		customer1.setSocialMedia(testSocialMedia.get(0));
		
		Customer customer2 = testCustomers.get(1);
		customer2.setAddress(address2);
		customer2.setSocialMedia(testSocialMedia.get(1));
		
		Customer customer3 = testCustomers.get(2);
		customer3.setAddress(address3);
		customer3.setSocialMedia(testSocialMedia.get(2));
		
		Customer customer4 = testCustomers.get(3);
		customer4.setAddress(address4);
		customer4.setSocialMedia(testSocialMedia.get(3));
		
		Customer customer5 = testCustomers.get(4);
		customer5.setAddress(address5);
		customer5.setSocialMedia(testSocialMedia.get(4));
		
		//Make employees
		Employee employee1 = testEmployees.get(0);
		employee1.setAddress(address1);
		employee1.setSocialMedia(testSocialMedia.get(0));
		
		Employee employee2 = testEmployees.get(1);
		employee2.setAddress(address2);
		employee2.setSocialMedia(testSocialMedia.get(1));
		
		Employee employee3 = testEmployees.get(2);
		employee3.setAddress(address3);
		employee3.setSocialMedia(testSocialMedia.get(2));
		
		Employee employee4 = testEmployees.get(3);
		employee4.setAddress(address4);
		employee4.setSocialMedia(testSocialMedia.get(3));
		
		Employee employee5 = testEmployees.get(4);
		employee5.setAddress(address5);
		employee5.setSocialMedia(testSocialMedia.get(4));
		
		//Make emp sets
		HashSet<Employee> caseSet1 = new HashSet<Employee>();
		caseSet1.add(employee1);
		HashSet<Employee> caseSet2 = new HashSet<Employee>();
		caseSet2.add(employee2);
		caseSet2.add(employee3);
		HashSet<Employee> caseSet3 = new HashSet<Employee>();
		caseSet3.add(employee1);
		caseSet3.add(employee3);
		caseSet3.add(employee5);
		HashSet<Employee> caseSet4 = new HashSet<Employee>();
		caseSet4.add(employee4);
		HashSet<Employee> caseSet5 = new HashSet<Employee>();
		caseSet5.add(employee2);
		HashSet<Employee> caseSet6 = new HashSet<Employee>();
		caseSet6.add(employee3);
		
		//Make cases
		Cases case1 = testCases.get(0);
		case1.setEmployee(caseSet1);
		case1.setCustomer(customer1);
		Cases case2 = testCases.get(1);
		case2.setEmployee(caseSet2);
		case2.setCustomer(customer2);
		Cases case3 = testCases.get(2);
		case3.setEmployee(caseSet3);
		case3.setCustomer(customer3);
		Cases case4 = testCases.get(3);
		case4.setEmployee(caseSet4);
		case4.setCustomer(customer4);
		Cases case5 = testCases.get(4);
		case5.setEmployee(caseSet5);
		case5.setCustomer(customer5);
		Cases case6 = testCases.get(5);
		case6.setEmployee(caseSet6);
		case6.setCustomer(customer1);
		
		//Sets for emps
		HashSet<Cases> empCases1 = new HashSet<Cases>();
		empCases1.add(case1);
		empCases1.add(case3);
		HashSet<Cases> empCases2 = new HashSet<Cases>();
		empCases2.add(case2);
		empCases2.add(case5);
		HashSet<Cases> empCases3 = new HashSet<Cases>();
		empCases3.add(case2);
		empCases3.add(case3);
		empCases3.add(case6);
		HashSet<Cases> empCases4 = new HashSet<Cases>();
		empCases4.add(case4);
		HashSet<Cases> empCases5 = new HashSet<Cases>();
		empCases5.add(case3);
		
		//Add sets to employees
		employee1.setCases(empCases1);
		employee2.setCases(empCases2);
		employee3.setCases(empCases3);
		employee4.setCases(empCases4);
		employee5.setCases(empCases5);
		
		employeeTestingSet.add(employee1);
		employeeTestingSet.add(employee2);
		employeeTestingSet.add(employee3);
		employeeTestingSet.add(employee4);
		employeeTestingSet.add(employee5);
	}
	
//	//GET
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployees() throws Exception{
		Mockito.when(employeeService.getEmployees()).thenReturn(employeeTestingSet);
		
		mockMvc.perform(MockMvcRequestBuilders
						.get("/employees")
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", hasSize(5)))
						.andExpect(content().json(mapper.writeValueAsString(employeeTestingSet)));
		
		verify(employeeService).getEmployees();
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeesEmptyList() throws Exception {
		List<Employee> emptyList = new ArrayList<Employee>();
		
		Mockito.when(employeeService.getEmployees()).thenReturn(emptyList);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)))
				.andExpect(content().json(mapper.writeValueAsString(emptyList)));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeByID() throws Exception{
		//wtf doesnt reset
		System.out.println(employeeTestingSet.get(0).getId());
		System.out.println(employeeTestingSet.get(0).getFirstName());
		Mockito.when(employeeService.getEmployeeByID(employeeTestingSet.get(0).getId())).thenReturn(employeeTestingSet.get(0));
		
		mockMvc.perform(MockMvcRequestBuilders
						.get("/employees/id").param("id", "1")
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", notNullValue()))
						.andExpect(jsonPath("$.firstName", is("Benjamin")));	
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeByIDNullParams() throws Exception{
		Mockito.when(employeeService.getEmployeeByID(null)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id").param("id", "").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeByInvalidParams() throws Exception {
		Mockito.when(employeeService.getEmployeeByID(55)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id").param("id", "55").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$", is("404 NOT FOUND: Entity not found due to invalid ID")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeAssignedCases() throws Exception{
		Mockito.when(employeeService.getEmployeeCasesByID(employeeTestingSet.get(2).getId())).thenReturn(new ArrayList<Cases>(employeeTestingSet.get(2).getCases()));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/cases").param("id","3").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(3)));
		
		Mockito.when(employeeService.getEmployeeCasesByID(employeeTestingSet.get(1).getId())).thenReturn(new ArrayList<Cases>(employeeTestingSet.get(1).getCases()));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/cases").param("id","2").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)));
		
		Mockito.when(employeeService.getEmployeeCasesByID(employeeTestingSet.get(0).getId())).thenReturn(new ArrayList<Cases>(employeeTestingSet.get(0).getCases()));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/cases").param("id","1").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeAssignedCasesEmptyList() throws Exception {
		Employee emp = employeeTestingSet.get(0);
		emp.setCases(new HashSet<Cases>());
		
		Mockito.when(employeeService.getEmployeeCasesByID(emp.getId())).thenReturn(new ArrayList<Cases>());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/cases").param("id", "1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeAssignedCustomers() throws Exception{
		List<Customer> customers = new ArrayList<Customer>();

		//Help
		employeeTestingSet.get(2).getCases().stream().forEach(cases->{
			if(!customers.contains(cases.getCustomer())) {
				customers.add(cases.getCustomer());
			}
		});
		Mockito.when(employeeService.getCustomersAssignedToEmployee(employeeTestingSet.get(2).getId())).thenReturn(customers);
		mockMvc.perform((MockMvcRequestBuilders.get("/employees/id/customers").param("id", "3").contentType(MediaType.APPLICATION_JSON)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(3)));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeAssignedCustomersEmptyList() throws Exception {
		Employee emp = employeeTestingSet.get(0);
		emp.setCases(new HashSet<Cases>());
		Mockito.when(employeeService.getCustomersAssignedToEmployee(emp.getId())).thenReturn(new ArrayList<Customer>());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/customers").param("id", "1").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
	}
	
	//employee->case->customer
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getCustomerFromEmployeeAssignedCase() throws Exception {
		//culprit
		Cases exCase = new Cases();
		for(Cases cases:employeeTestingSet.get(2).getCases()) {
			if(cases.getId()==2) {
				exCase = cases;
				break;
			}
		}
		Mockito.when(employeeService.getCustomerFromEmployeeAssignedCase(3, 2)).thenReturn(exCase.getCustomer());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/cases/customer").param("empId", "3").param("caseId", "2").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(2)));
	}
	
	//POST
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveNewEmployee() throws Exception {
		Employee emp = employeeTestingSet.get(0);
		emp.setId(0);
		emp.setFirstName("John");
		emp.setMiddleName("Sam");
		emp.setLastName("Smith");
		
		Mockito.when(employeeService.saveEmployeeDetails(any(Employee.class))).thenReturn(emp);
		
		String expectedBody = mapper.writeValueAsString(emp);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.accept(MediaType.APPLICATION_JSON);
		
        mockMvc.perform(mockRequest)
		        .andExpect(status().isCreated())
		        .andExpect(content().json(expectedBody));
//        verify(employeeService.saveEmployeeDetails(emp),times(1));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveEmployee() {
		
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveNewAddressToEmployee() {
		
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveNewCaseToEmployee() {
		
	}
	
	//PUT
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateFullEmployee() {
		
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateEmployeeAddress() {
		
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateEmployeeSocialMedia() {
		
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateEmployeeStats() {
		
	}
	
	//DELETE
}
