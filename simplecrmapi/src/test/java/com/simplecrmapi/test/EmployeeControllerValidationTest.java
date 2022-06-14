package com.simplecrmapi.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
import org.springframework.boot.test.context.SpringBootTest;
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

//@WebMvcTest(EmployeeController.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class EmployeeControllerValidationTest {
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
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerSuccess() throws Exception{
		Employee emp = employeeTestingSet.get(1);
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
		
		verify(employeeService).saveEmployeeDetails(any(Employee.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullEmployeeFailed() throws Exception{
		Employee emp = employeeTestingSet.get(1);
		emp.setFirstName("John123");
		emp.setMiddleName("Sam123");
		emp.setLastName("Smith123");
		
		Mockito.when(employeeService.saveEmployeeDetails(any(Employee.class))).thenReturn(emp);
		String expectedBody = mapper.writeValueAsString(emp);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$", containsString("Input error in field lastName with value Smith123. Please use alphabetical characters only")))
		.andExpect(jsonPath("$", containsString("Input error in field middleName with value Sam123. Please use alphabetical characters only")))
		.andExpect(jsonPath("$", containsString("Input error in field firstName with value John123. Please use alphabetical characters only")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullEmployeeFailedDate() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		emp.setDateOfBirth(LocalDate.now());
		
		String body = mapper.writeValueAsString(emp);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees")
																	.contentType(MediaType.APPLICATION_JSON)
																	.content(body)
																	.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", containsString("400 BAD REQUEST: Validation failed due to: [Input error in field dateOfBirth with value")))
				.andExpect(jsonPath("$", containsString(". Date has to be in the past]")));

				
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullEmployeeFailedPhoneNumberType() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		emp.setPhoneNumber("aaaaaaaaaaaa");
		String body = mapper.writeValueAsString(emp);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees")
																	.contentType(MediaType.APPLICATION_JSON)
																	.content(body)
																	.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Validation failed due to: [Input error in field phoneNumber with value aaaaaaaaaaaa. Please use numbers only]")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullEmployeeFailedEmail() throws Exception{
		Employee emp =  employeeTestingSet.get(1);
		emp.setEmailAddress("test");
		String body = mapper.writeValueAsString(emp);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees")
																	.contentType(MediaType.APPLICATION_JSON)
																	.content(body)
																	.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Validation failed due to: [Input error in field emailAddress with value test. Please input a valid email address]")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullEmployeeCaseNumbersValidation() throws Exception{
		Employee emp = employeeTestingSet.get(1);
		emp.setCasesActive(1);
		emp.setCasesPending(2);
		emp.setCasesResolved(3);
		emp.setCasesClosed(4);
		
		Mockito.when(employeeService.saveEmployeeDetails(any(Employee.class))).thenReturn(emp);
		String body = mapper.writeValueAsString(emp);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(body)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(content().json(body));
		
		verify(employeeService,times(1)).saveEmployeeDetails(any(Employee.class));
		
		emp.setCasesActive(-1);
		emp.setCasesPending(-2);
		emp.setCasesResolved(-3);
		emp.setCasesClosed(-4);
		body = mapper.writeValueAsString(emp);
		
		mockRequest = MockMvcRequestBuilders.post("/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body)
				.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpectAll(status().isBadRequest(),
						jsonPath("$",containsString("Input error in field casesPending with value -2. Please input a number greater than or equal to 0")),
						jsonPath("$", containsString("Input error in field casesActive with value -1. Please input a number greater than or equal to 0")),
						jsonPath("$", containsString("Input error in field casesResolved with value -3. Please input a number greater than or equal to 0")),
						jsonPath("$", containsString("Input error in field casesClosed with value -4. Please input a number greater than or equal to 0"))
				);
	}
}