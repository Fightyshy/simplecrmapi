package com.simplecrmapi.test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.internal.matchers.GreaterThan;
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
import com.simplecrmapi.enums.CaseStatus;
import com.simplecrmapi.rest.EmployeeController;
import com.simplecrmapi.service.EmployeeService;
import com.simplecrmapi.test.util.CSVParser;
import com.simplecrmapi.util.EntityNotFound;
import com.simplecrmapi.util.InvalidParamsException;

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
		
		verify(employeeService).getEmployees();
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeByID() throws Exception{
		Mockito.when(employeeService.getEmployeeByID(1)).thenReturn(employeeTestingSet.get(0));
		
		mockMvc.perform(MockMvcRequestBuilders
						.get("/employees/id").param("id", "1")
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", notNullValue()))
						.andExpect(jsonPath("$.firstName", is("Benjamin")));
		
		verify(employeeService).getEmployeeByID(1);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeByIDNullParams() throws Exception{
		Mockito.when(employeeService.getEmployeeByID(null)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id").param("id", "").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		//No need?
//		verify(employeeService).getEmployeeByID(null);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeByInvalidParams() throws Exception {
		Mockito.when(employeeService.getEmployeeByID(55)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id").param("id", "55").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$", is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(employeeService).getEmployeeByID(55);
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
		
		verify(employeeService, times(3)).getEmployeeCasesByID(any(int.class));
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
		
		verify(employeeService, times(1)).getEmployeeCasesByID(1);
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
		
		verify(employeeService, times(1)).getCustomersAssignedToEmployee(3);
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
		
		verify(employeeService, times(1)).getCustomersAssignedToEmployee(1);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getEmployeeAssignedCustomersInvalidParam() throws Exception {
		Employee emp = employeeTestingSet.get(0);
		
		Mockito.when(employeeService.getCustomersAssignedToEmployee(55)).thenThrow(new InvalidParamsException());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/customers").param("id", "55").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		verify(employeeService, times(1)).getCustomersAssignedToEmployee(55);
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
		
		verify(employeeService, times(1)).getCustomerFromEmployeeAssignedCase(3,2);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getCustomerFromEmployeeAssignedCaseInvalidParam() throws Exception {
		Mockito.when(employeeService.getCustomerFromEmployeeAssignedCase(321, 55)).thenThrow(new InvalidParamsException());
		
		mockMvc.perform((MockMvcRequestBuilders.get("/employees/id/cases/customer").param("empId", "321").param("caseId", "55").contentType(MediaType.APPLICATION_JSON)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		verify(employeeService, times(1)).getCustomerFromEmployeeAssignedCase(321, 55);
	}
	
	//POST
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveNewEmployee() throws Exception {
		Employee emp = new Employee();
		emp.setFirstName("John");
		emp.setMiddleName("Sam");
		emp.setLastName("Smith");
		emp.setDateOfBirth(LocalDate.of(1995, 12, 19));
		emp.setPhoneNumber("123456789012");
		emp.setEmailAddress("test@test.com");
		emp.setCasesActive(0);
		emp.setCasesPending(0);
		emp.setCasesResolved(0);
		emp.setCasesClosed(0);
		emp.setAddress(employeeTestingSet.get(0).getAddress());
		emp.setSocialMedia(new SocialMedia());
		Employee empFin = emp;
		empFin.setId(6);

		Mockito.when(employeeService.saveEmployeeDetails(any(Employee.class))).thenReturn(empFin);
		
		String expectedBody = mapper.writeValueAsString(emp);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.accept(MediaType.APPLICATION_JSON);
		
        mockMvc.perform(mockRequest)
        		.andDo(print())
		        .andExpect(status().isCreated())
		        .andExpect(content().json(mapper.writeValueAsString(empFin)))
        		.andExpect(jsonPath("$.id", is(6)));
        verify(employeeService,times(1)).saveEmployeeDetails(any(Employee.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveNewAddressToEmployee() throws Exception {
		Address add = employeeTestingSet.get(1).getAddress().get(0);
		add.setId(0);
		
		Mockito.when(employeeService.saveNewAddressToEmployee(any(Address.class), any(int.class))).thenReturn(add);
		
		String expectedBody = mapper.writeValueAsString(add);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees/id/addresses").param("id", "1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService,times(1)).saveNewAddressToEmployee(any(Address.class), any(int.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveNewCaseToEmployee() throws Exception {
		Cases testCase = employeeTestingSet.get(4).getCases().iterator().next();
		testCase.setId(0);
		
		Mockito.when(employeeService.saveNewCaseToEmployee(any(Cases.class), any(int.class))).thenReturn(testCase);
		String expectedBody = mapper.writeValueAsString(testCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees/id/cases").param("id", "1")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expectedBody)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService,times(1)).saveNewCaseToEmployee(any(Cases.class), any(int.class));
	}
	
	//PUT
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateFullEmployee() throws Exception {
		Employee emp = employeeTestingSet.get(2);
		emp.setFirstName("John");
		emp.setLastName("Smith");
		emp.setPhoneNumber("123456789012");
		
		Mockito.when(employeeService.saveEmployeeDetails(any(Employee.class))).thenReturn(emp);
		String expectedBody = mapper.writeValueAsString(emp);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/employees")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expectedBody)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(content().json(expectedBody));
		
		verify(employeeService, times(1)).saveEmployeeDetails(any(Employee.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateEmployeeAddressByID() throws Exception {
		Address add = employeeTestingSet.get(1).getAddress().get(0);
		add.setCity("SomewhereElse");
		add.setLine1("Different Building Name");
		
		Mockito.when(employeeService.updateEmployeeAddressByID(any(Address.class), any(int.class))).thenReturn(add);
		String expectedBody = mapper.writeValueAsString(add);
		MockHttpServletRequestBuilder MockRequest = MockMvcRequestBuilders.put("/employees/id/addresses").param("id", "2")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expectedBody)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(MockRequest)
				.andExpect(status().isOk())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService, times(1)).updateEmployeeAddressByID(any(Address.class),any(int.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateEmployeeAddressByIDInvalidID() throws Exception {
		Mockito.when(employeeService.updateEmployeeAddressByID(any(Address.class), any(int.class))).thenThrow(new InvalidParamsException());
		mockMvc.perform(MockMvcRequestBuilders.put("/employees/id/addresses").param("id", "555")
											.contentType(MediaType.APPLICATION_JSON)
											.content(mapper.writeValueAsString(employeeTestingSet.get(0).getAddress().get(0)))
											.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		verify(employeeService, times(1)).updateEmployeeAddressByID(any(Address.class),any(int.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateEmployeeAssignedCaseByID() throws Exception {
		Cases cases = employeeTestingSet.get(0).getCases().iterator().next();
		cases.setCasesStatus(CaseStatus.RESOLVED.toString());
		cases.setEndDate(LocalDateTime.now());
		
		Mockito.when(employeeService.updateEmployeeAssignedCaseByID(any(Cases.class), any(int.class))).thenReturn(cases);
		String expectedBody = mapper.writeValueAsString(cases);
		MockHttpServletRequestBuilder MockRequest = MockMvcRequestBuilders.put("/employees/id/cases").param("id", "1")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expectedBody)
																			.contentType(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(MockRequest)
				.andExpect(status().isOk())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService, times(1)).updateEmployeeAssignedCaseByID(any(Cases.class),any(int.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateEmployeeAssignedCaseByIDInvalidID() throws Exception {
		Mockito.when(employeeService.updateEmployeeAssignedCaseByID(any(Cases.class), any(int.class))).thenThrow(new InvalidParamsException());
		mockMvc.perform(MockMvcRequestBuilders.put("/employees/id/cases").param("id", "555")
											.contentType(MediaType.APPLICATION_JSON)
											.content(mapper.writeValueAsString(employeeTestingSet.get(0).getCases().iterator().next()))
											.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		verify(employeeService, times(1)).updateEmployeeAssignedCaseByID(any(Cases.class),any(int.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateEmployeeSocialMedia() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		emp.setSocialMedia(new SocialMedia());
		
		Mockito.when(employeeService.updateEmployeeSocialMedia(any(SocialMedia.class), any(int.class))).thenReturn(emp.getSocialMedia());
		String expectedBody = mapper.writeValueAsString(emp.getSocialMedia());
		MockHttpServletRequestBuilder MockRequest = MockMvcRequestBuilders.put("/employees/id/socialmedia").param("id", "1")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expectedBody)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(MockRequest)
				.andExpect(status().isOk())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService, times(1)).updateEmployeeSocialMedia(any(SocialMedia.class), any(int.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateEmployeeSocialMediaInvalidID() throws Exception {
		Mockito.when(employeeService.updateEmployeeSocialMedia(any(SocialMedia.class), any(int.class))).thenThrow(new InvalidParamsException());
		mockMvc.perform(MockMvcRequestBuilders.put("/employees/id/socialmedia").param("id", "555")
											.contentType(MediaType.APPLICATION_JSON)
											.content(mapper.writeValueAsString(employeeTestingSet.get(0).getSocialMedia()))
											.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		verify(employeeService, times(1)).updateEmployeeSocialMedia(any(SocialMedia.class), any(int.class));
	}
	
	//TODO Need to think
	//Concept depreciated, can update via full employee endpoints /w validation support
//	@Test
//	@WithMockUser(username="john", roles= {"CUSTOMER"})
//	void updateEmployeeStats() {
//		
//	}
	
	//DELETE
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void deleteEmployee() throws Exception {
		Mockito.doNothing().when(employeeService).deleteEmployeeByID(employeeTestingSet.get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/employees/id").param("id", "1")
																		.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNoContent());
		
		verify(employeeService, times(1)).deleteEmployeeByID(1);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void deleteEmployeeInvalidParams() throws Exception {
		Mockito.doThrow(new InvalidParamsException()).when(employeeService).deleteEmployeeByID(555);
		mockMvc.perform(MockMvcRequestBuilders.delete("/employees/id").param("id", "555").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		verify(employeeService, times(1)).deleteEmployeeByID(555);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void deleteEmployeeCase() throws Exception {
		//id=1 case of id=1 employee
//		Mockito.doNothing().when(employeeService).removeEmployeeAssignedCase(employeeTestingSet.get(0).getId(), employeeTestingSet.get(0).getCases().iterator().next().getId());
		Mockito.doNothing().when(employeeService).removeEmployeeAssignedCase(1, 3);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/employees/id/cases").param("employeeId", "1").param("caseId", "3")
																		.contentType(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent());
		
		verify(employeeService, times(1)).removeEmployeeAssignedCase(1,3);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void deleteEmployeeCaseInvalidParams() throws Exception {
		Mockito.doThrow(new InvalidParamsException()).when(employeeService).removeEmployeeAssignedCase(555, 555);
		mockMvc.perform(MockMvcRequestBuilders.delete("/employees/id/cases").param("employeeId", "555").param("caseId", "555").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		verify(employeeService, times(1)).removeEmployeeAssignedCase(555,555);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void deleteEmployeeAddress() throws Exception{
		Mockito.doNothing().when(employeeService).deleteEmployeeAddressByIDs(employeeTestingSet.get(1).getId(),employeeTestingSet.get(1).getAddress().get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/employees/id/addresses").param("employeeId", "2").param("addressId", "1")
																			.contentType(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNoContent());
		
		verify(employeeService, times(1)).deleteEmployeeAddressByIDs(2, 1);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void deleteEmployeeAddressInvalidParams() throws Exception {
		Mockito.doThrow(new InvalidParamsException()).when(employeeService).deleteEmployeeAddressByIDs(555, 555);
		mockMvc.perform(MockMvcRequestBuilders.delete("/employees/id/addresses").param("employeeId", "555").param("addressId", "555").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		
		verify(employeeService, times(1)).deleteEmployeeAddressByIDs(555,555);
	}
}
