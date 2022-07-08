package com.simplecrmapi.test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecrmapi.dao.EmployeeDAO;
import com.simplecrmapi.dao.UserDAO;
import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.entity.Role;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.entity.User;
import com.simplecrmapi.enums.CaseStatus;
import com.simplecrmapi.rest.EmployeeController;
import com.simplecrmapi.rest.UserController;
import com.simplecrmapi.service.EmployeeService;
import com.simplecrmapi.service.UserService;
import com.simplecrmapi.test.util.CSVParser;
import com.simplecrmapi.util.EntityNotFound;
import com.simplecrmapi.util.InvalidParamsException;
import com.simplecrmapi.util.JwtAuthenticationFilter;
import com.simplecrmapi.util.TokenProvider;
import com.simplecrmapi.util.UnauthorizedEntryPoint;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class EmployeeControllerLiveTest {
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
	
	@InjectMocks
	private UserController userController;
	
	@MockBean(name="userService")
	private UserDetailsService userDetailsService;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private UserDAO userDAO;
	
	@MockBean
	private UnauthorizedEntryPoint unAuthorizedEntryPoint;
	
	@MockBean
	private TokenProvider token;
	
	@Autowired
	private JwtAuthenticationFilter fliter;
	
	@MockBean
    private AuthenticationManager authenticationManager;
	
	private List<Employee> employeeTestingSet = new ArrayList<>();
	
	private String genToken;
	
	private User emp1;
	
	private User emp2;
	
	@BeforeEach //once before every test
	void init() {
		//Smoking gun https://stackoverflow.com/questions/44849827/mock-securitycontextholder-authentication-always-returning-null
	    Authentication authentication = mock(Authentication.class);
	    SecurityContext securityContext = mock(SecurityContext.class);
	    
	    genToken = token.generateToken(SecurityContextHolder.getContext().getAuthentication());
	    
		employeeTestingSet = new ArrayList<>(); //clear to reset
		CSVParser parser = new CSVParser();
		List<String[]> employeeFile = parser.readLine("Employee.txt");
		List<Employee> testEmployees = parser.employeeParser(employeeFile);
		
		List<String[]> customerFile = parser.readLine("Customer.txt");
		List<Customer> testCustomers = parser.customerParser(customerFile);
		
		List<String[]> CasesFile = parser.readLine("Cases.txt");
		List<Cases> testCases = parser.caseParser(CasesFile);
		
		List<String[]> addressFile = parser.readLine("Address.txt");
		List<Address> testAddresses = parser.addressParser(addressFile);
		
		List<String[]> socialMedia = parser.readLine("SocialMedia.txt");
		List<SocialMedia> testSocialMedia = parser.socialMediaParser(socialMedia);
		
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
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getEmployees() throws Exception{
		Mockito.when(employeeService.getEmployees()).thenReturn(employeeTestingSet);
		
		mockMvc.perform(MockMvcRequestBuilders
						.get("/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer "+genToken))
						.andDo(print())
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", hasSize(5)))
						.andExpect(content().json(mapper.writeValueAsString(employeeTestingSet)));
		
		verify(employeeService).getEmployees();
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getEmployeesEmptyList() throws Exception {
		List<Employee> emptyList = new ArrayList<Employee>();
		
		Mockito.when(employeeService.getEmployees()).thenReturn(emptyList);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)))
				.andExpect(content().json(mapper.writeValueAsString(emptyList)));
		
		verify(employeeService).getEmployees();
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getEmployeeByID() throws Exception{
		Mockito.when(employeeService.getEmployeeByID(1)).thenReturn(employeeTestingSet.get(0));
		
		mockMvc.perform(MockMvcRequestBuilders
						.get("/employees/id").param("id", "1")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer "+genToken))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", notNullValue()))
						.andExpect(jsonPath("$.firstName", is("Benjamin")));
		
		verify(employeeService).getEmployeeByID(1);
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getEmployeeByIDNullParams() throws Exception{
		Mockito.when(employeeService.getEmployeeByID(null)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id").param("id", "").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		//No need?
//		verify(employeeService).getEmployeeByID(null);
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getEmployeeByInvalidParams() throws Exception {
		Mockito.when(employeeService.getEmployeeByID(55)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id").param("id", "55").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$", is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(employeeService).getEmployeeByID(55);
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getEmployeeAssignedCases() throws Exception{
		Mockito.when(employeeService.getEmployeeCasesByID(employeeTestingSet.get(2).getId())).thenReturn(new ArrayList<Cases>(employeeTestingSet.get(2).getCases()));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/cases").param("id","3").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(3)));
		
		Mockito.when(employeeService.getEmployeeCasesByID(employeeTestingSet.get(1).getId())).thenReturn(new ArrayList<Cases>(employeeTestingSet.get(1).getCases()));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/cases").param("id","2").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)));
		
		Mockito.when(employeeService.getEmployeeCasesByID(employeeTestingSet.get(0).getId())).thenReturn(new ArrayList<Cases>(employeeTestingSet.get(0).getCases()));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/cases").param("id","1").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)));
		
		verify(employeeService, times(3)).getEmployeeCasesByID(any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getEmployeeAssignedCasesEmptyList() throws Exception {
		Employee emp = employeeTestingSet.get(0);
		emp.setCases(new HashSet<Cases>());
		
		Mockito.when(employeeService.getEmployeeCasesByID(emp.getId())).thenReturn(new ArrayList<Cases>());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/cases").param("id", "1").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
		
		verify(employeeService, times(1)).getEmployeeCasesByID(1);
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getEmployeeAssignedCustomers() throws Exception{
		List<Customer> customers = new ArrayList<Customer>();

		//Help
		employeeTestingSet.get(2).getCases().stream().forEach(cases->{
			if(!customers.contains(cases.getCustomer())) {
				customers.add(cases.getCustomer());
			}
		});
		Mockito.when(employeeService.getCustomersAssignedToEmployee(employeeTestingSet.get(2).getId())).thenReturn(customers);
		mockMvc.perform((MockMvcRequestBuilders.get("/employees/id/customers").param("id", "3").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(3)));
		
		verify(employeeService, times(1)).getCustomersAssignedToEmployee(3);
	}

	
	
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getEmployeeAssignedCustomersEmptyList() throws Exception {
		Employee emp = employeeTestingSet.get(0);
		emp.setCases(new HashSet<Cases>());
		Mockito.when(employeeService.getCustomersAssignedToEmployee(emp.getId())).thenReturn(new ArrayList<Customer>());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/customers").param("id", "1").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
		
		verify(employeeService, times(1)).getCustomersAssignedToEmployee(1);
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getEmployeeAssignedCustomersInvalidParam() throws Exception {
		Employee emp = employeeTestingSet.get(0);
		
		Mockito.when(employeeService.getCustomersAssignedToEmployee(55)).thenThrow(new InvalidParamsException());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/customers").param("id", "55").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		verify(employeeService, times(1)).getCustomersAssignedToEmployee(55);
	}
	
	//employee->case->customer
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
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
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/id/cases/customer").param("empId", "3").param("caseId", "2").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(2)));
		
		verify(employeeService, times(1)).getCustomerFromEmployeeAssignedCase(3,2);
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCustomerFromEmployeeAssignedCaseInvalidParam() throws Exception {
		Mockito.when(employeeService.getCustomerFromEmployeeAssignedCase(321, 55)).thenThrow(new InvalidParamsException());
		
		mockMvc.perform((MockMvcRequestBuilders.get("/employees/id/cases/customer").param("empId", "321").param("caseId", "55").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		verify(employeeService, times(1)).getCustomerFromEmployeeAssignedCase(321, 55);
	}
	
	//Principal-GET
	
	@Test
	@WithUserDetails(value="employee1", userDetailsServiceBeanName="testEmployeeDetails")
	void getEmployeeFromUserDetails() throws Exception{
		Employee emp = employeeTestingSet.get(0);

		Mockito.when(employeeService.getEmployeeByID(any(Integer.class))).thenReturn(emp);
		String expected = mapper.writeValueAsString(emp);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/employees/users").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer" + genToken);//.with(user(emp1));
		
		mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(content().json(expected));
		
		verify(employeeService, times(1)).getEmployeeByID(any(Integer.class));
	}
	
	@Test
	@WithUserDetails(value="employee1", userDetailsServiceBeanName="testEmployeeDetails")
	void getEmployeeAssignedCasesFromUserDetails() throws Exception{
		Employee emp = employeeTestingSet.get(0);

		Mockito.when(employeeService.getEmployeeByID(any(Integer.class))).thenReturn(emp);
		String expected = mapper.writeValueAsString(emp.getCases());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/employees/users/cases").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer" + genToken);//.with(user(emp1));
		
		mockMvc.perform(request)
		.andExpect(status().isOk())
		.andExpect(content().json(expected));
		
		verify(employeeService, times(1)).getEmployeeByID(any(Integer.class));
	}
	
	@Test
	@WithUserDetails(value="employee1", userDetailsServiceBeanName="testEmployeeDetails")
	void getEmployeeAssignedCustomersFromUserDetails() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		
		Mockito.when(employeeService.getEmployeeByID(any(Integer.class))).thenReturn(emp);
		
		List<Customer> customers = new ArrayList<Customer>();

		emp.getCases().stream().forEach(cases->{
			if(!customers.contains(cases.getCustomer())) {
				customers.add(cases.getCustomer());
			}
		});
		String expected = mapper.writeValueAsString(customers);
		Mockito.when(employeeService.getCustomersAssignedToEmployee(emp)).thenReturn(customers);
		mockMvc.perform((MockMvcRequestBuilders.get("/employees/users/customers").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken)))
			.andExpect(status().isOk())
			.andExpect(content().json(expected));
		
		verify(employeeService, times(1)).getEmployeeByID(any(Integer.class));
		verify(employeeService, times(1)).getCustomersAssignedToEmployee(any(Employee.class));
	}
	
	@Test
	@WithUserDetails(value="employee1", userDetailsServiceBeanName="testEmployeeDetails")
	void getEmployeeAssignedToUserEmployeCase() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		Customer cus = emp.getCases().iterator().next().getCustomer();
		
		Mockito.when(employeeService.getEmployeeByID(any(Integer.class))).thenReturn(emp);
		Mockito.when(employeeService.getCustomerFromEmployeeAssignedCase(any(Employee.class),any(Integer.class))).thenReturn(cus);
		
		String expected = mapper.writeValueAsString(cus);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/employees/users/cases/customer").param("caseId", "1").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
		.andExpect(status().isOk())
		.andExpect(content().json(expected));
		
		verify(employeeService, times(1)).getEmployeeByID(any(Integer.class));
		verify(employeeService, times(1)).getCustomerFromEmployeeAssignedCase(any(Employee.class), any(Integer.class));
	}
	
	//POST
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
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
													.header("Authorization", "Bearer "+genToken)
													.accept(MediaType.APPLICATION_JSON);
		
        mockMvc.perform(mockRequest)
        		.andDo(print())
		        .andExpect(status().isCreated())
		        .andExpect(content().json(mapper.writeValueAsString(empFin)))
        		.andExpect(jsonPath("$.id", is(6)));
        verify(employeeService,times(1)).saveEmployeeDetails(any(Employee.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void saveNewAddressToEmployee() throws Exception {
		Address add = employeeTestingSet.get(1).getAddress().get(0);
		add.setId(0);
		
		Mockito.when(employeeService.saveNewAddressToEmployee(any(Address.class), any(int.class))).thenReturn(add);
		
		String expectedBody = mapper.writeValueAsString(add);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees/id/addresses").param("id", "1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService,times(1)).saveNewAddressToEmployee(any(Address.class), any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void saveNewCaseToEmployee() throws Exception {
		Cases testCase = employeeTestingSet.get(4).getCases().iterator().next();
		testCase.setId(0);
		
		Mockito.when(employeeService.saveNewCaseToEmployee(any(Cases.class), any(int.class))).thenReturn(testCase);
		String expectedBody = mapper.writeValueAsString(testCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees/id/cases").param("id", "1")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expectedBody)
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService,times(1)).saveNewCaseToEmployee(any(Cases.class), any(int.class));
	}
	
	//Principal-based POST
	
	@Test
	@WithUserDetails(value="employee1", userDetailsServiceBeanName="testEmployeeDetails")
	void saveNewAddressToUserEmployee() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		Address add = employeeTestingSet.get(1).getAddress().get(0);
		add.setId(0);
		
		Mockito.when(employeeService.getEmployeeByID(any(Integer.class))).thenReturn(emp);
		String expected = mapper.writeValueAsString(add);
		Mockito.when(employeeService.saveNewAddressToEmployee(any(Address.class), any(Employee.class))).thenReturn(add);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees/users/addresses")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expected)
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(expected));
		
		verify(employeeService, times(1)).getEmployeeByID(any(Integer.class));
		verify(employeeService, times(1)).saveNewAddressToEmployee(any(Address.class), any(Employee.class));
	}
	
	@Test
	@WithUserDetails(value="employee1", userDetailsServiceBeanName="testEmployeeDetails")
	void saveNewCaseToUserEmployee() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		Cases testCase = employeeTestingSet.get(4).getCases().iterator().next();
		testCase.setId(0);
		
		Mockito.when(employeeService.getEmployeeByID(any(Integer.class))).thenReturn(emp);
		Mockito.when(employeeService.saveNewCaseToEmployee(any(Cases.class), any(Employee.class))).thenReturn(testCase);
		String expectedBody = mapper.writeValueAsString(testCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/employees/users/cases")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expectedBody)
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService, times(1)).getEmployeeByID(any(Integer.class));
		verify(employeeService,times(1)).saveNewCaseToEmployee(any(Cases.class), any(Employee.class));
	}
	
	//PUT
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
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
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(content().json(expectedBody));
		
		verify(employeeService, times(1)).saveEmployeeDetails(any(Employee.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void updateEmployeeAddressByID() throws Exception {
		Address add = employeeTestingSet.get(1).getAddress().get(0);
		add.setCity("SomewhereElse");
		add.setLine1("Different Building Name");
		
		Mockito.when(employeeService.updateEmployeeAddressByID(any(Address.class), any(int.class))).thenReturn(add);
		String expectedBody = mapper.writeValueAsString(add);
		MockHttpServletRequestBuilder MockRequest = MockMvcRequestBuilders.put("/employees/id/addresses").param("id", "2")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expectedBody)
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(MockRequest)
				.andExpect(status().isOk())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService, times(1)).updateEmployeeAddressByID(any(Address.class),any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void updateEmployeeAddressByIDInvalidID() throws Exception {
		Mockito.when(employeeService.updateEmployeeAddressByID(any(Address.class), any(int.class))).thenThrow(new InvalidParamsException());
		mockMvc.perform(MockMvcRequestBuilders.put("/employees/id/addresses").param("id", "555")
											.contentType(MediaType.APPLICATION_JSON)
											.content(mapper.writeValueAsString(employeeTestingSet.get(0).getAddress().get(0)))
											.header("Authorization", "Bearer "+genToken)
											.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		verify(employeeService, times(1)).updateEmployeeAddressByID(any(Address.class),any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void updateEmployeeAssignedCaseByID() throws Exception {
		Cases cases = employeeTestingSet.get(0).getCases().iterator().next();
		cases.setCasesStatus(CaseStatus.RESOLVED.toString());
		cases.setEndDate(LocalDateTime.now());
		
		Mockito.when(employeeService.updateEmployeeAssignedCaseByID(any(Cases.class), any(int.class))).thenReturn(cases);
		String expectedBody = mapper.writeValueAsString(cases);
		MockHttpServletRequestBuilder MockRequest = MockMvcRequestBuilders.put("/employees/id/cases").param("id", "1")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expectedBody)
																			.header("Authorization", "Bearer "+genToken)
																			.contentType(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(MockRequest)
				.andExpect(status().isOk())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService, times(1)).updateEmployeeAssignedCaseByID(any(Cases.class),any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void updateEmployeeAssignedCaseByIDInvalidID() throws Exception {
		Mockito.when(employeeService.updateEmployeeAssignedCaseByID(any(Cases.class), any(int.class))).thenThrow(new InvalidParamsException());
		mockMvc.perform(MockMvcRequestBuilders.put("/employees/id/cases").param("id", "555")
											.contentType(MediaType.APPLICATION_JSON)
											.content(mapper.writeValueAsString(employeeTestingSet.get(0).getCases().iterator().next()))
											.header("Authorization", "Bearer "+genToken)
											.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		verify(employeeService, times(1)).updateEmployeeAssignedCaseByID(any(Cases.class),any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void updateEmployeeSocialMedia() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		emp.setSocialMedia(new SocialMedia());
		
		Mockito.when(employeeService.updateEmployeeSocialMedia(any(SocialMedia.class), any(int.class))).thenReturn(emp.getSocialMedia());
		String expectedBody = mapper.writeValueAsString(emp.getSocialMedia());
		MockHttpServletRequestBuilder MockRequest = MockMvcRequestBuilders.put("/employees/id/socialmedia").param("id", "1")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(expectedBody)
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(MockRequest)
				.andExpect(status().isOk())
				.andExpect(content().json(expectedBody));
		
		verify(employeeService, times(1)).updateEmployeeSocialMedia(any(SocialMedia.class), any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void updateEmployeeSocialMediaInvalidID() throws Exception {
		Mockito.when(employeeService.updateEmployeeSocialMedia(any(SocialMedia.class), any(int.class))).thenThrow(new InvalidParamsException());
		mockMvc.perform(MockMvcRequestBuilders.put("/employees/id/socialmedia").param("id", "555")
											.contentType(MediaType.APPLICATION_JSON)
											.content(mapper.writeValueAsString(employeeTestingSet.get(0).getSocialMedia()))
											.header("Authorization", "Bearer "+genToken)
											.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
		verify(employeeService, times(1)).updateEmployeeSocialMedia(any(SocialMedia.class), any(int.class));
	}
	
	//DELETE
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteEmployee() throws Exception {
		Mockito.doNothing().when(employeeService).deleteEmployeeByID(employeeTestingSet.get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/employees/id").param("id", "1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNoContent());
		
		verify(employeeService, times(1)).deleteEmployeeByID(1);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteEmployeeInvalidParams() throws Exception {
		Mockito.doThrow(new InvalidParamsException()).when(employeeService).deleteEmployeeByID(555);
		mockMvc.perform(MockMvcRequestBuilders.delete("/employees/id").param("id", "555").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
				.andExpect(status().isBadRequest());
		
		verify(employeeService, times(1)).deleteEmployeeByID(555);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteEmployeeCase() throws Exception {
		//id=1 case of id=1 employee
		Mockito.doNothing().when(employeeService).removeEmployeeAssignedCase(1, 3);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/employees/id/cases").param("employeeId", "1").param("caseId", "3")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent());
		
		verify(employeeService, times(1)).removeEmployeeAssignedCase(1,3);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteEmployeeCaseInvalidParams() throws Exception {
		Mockito.doThrow(new InvalidParamsException()).when(employeeService).removeEmployeeAssignedCase(555, 555);
		mockMvc.perform(MockMvcRequestBuilders.delete("/employees/id/cases").param("employeeId", "555").param("caseId", "555").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
				.andExpect(status().isBadRequest());
		
		verify(employeeService, times(1)).removeEmployeeAssignedCase(555,555);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteEmployeeAddress() throws Exception{
		Mockito.doNothing().when(employeeService).deleteEmployeeAddressByIDs(employeeTestingSet.get(1).getId(),employeeTestingSet.get(1).getAddress().get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/employees/id/addresses").param("employeeId", "2").param("addressId", "1")
																			.contentType(MediaType.APPLICATION_JSON)
																			.header("Authorization", "Bearer "+genToken);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNoContent());
		
		verify(employeeService, times(1)).deleteEmployeeAddressByIDs(2, 1);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteEmployeeAddressInvalidParams() throws Exception {
		Mockito.doThrow(new InvalidParamsException()).when(employeeService).deleteEmployeeAddressByIDs(555, 555);
		mockMvc.perform(MockMvcRequestBuilders.delete("/employees/id/addresses").param("employeeId", "555").param("addressId", "555").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer "+genToken))
				.andExpect(status().isBadRequest());
		
		verify(employeeService, times(1)).deleteEmployeeAddressByIDs(555,555);
	}
}
