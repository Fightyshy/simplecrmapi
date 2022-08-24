package com.simplecrmapi.test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecrmapi.dao.CasesDAO;
import com.simplecrmapi.dao.UserDAO;
import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Cases;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.entity.Products;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.rest.CaseController;
import com.simplecrmapi.rest.EmployeeController;
import com.simplecrmapi.rest.UserController;
import com.simplecrmapi.service.CasesService;
import com.simplecrmapi.service.EmployeeService;
import com.simplecrmapi.service.UserService;
import com.simplecrmapi.test.util.CSVParser;
import com.simplecrmapi.util.EntityNotFound;
import com.simplecrmapi.util.InvalidParamsException;
import com.simplecrmapi.util.JwtAuthenticationFilter;
import com.simplecrmapi.util.TokenProvider;
import com.simplecrmapi.util.UnauthorizedEntryPoint;

@WebMvcTest(CaseController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class CasesTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	private CasesService casesService;
	
	@MockBean
	private CasesDAO casesDAO;
	
	@InjectMocks
	private EmployeeController employeeController;
	
	@InjectMocks
	private UserController userController;
	
	@MockBean
	private EmployeeService employeeService;
	
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
	
	@MockBean
	private BCryptPasswordEncoder encoder;
	
	private String genToken;
	
	private List<Employee> employeeTestingSet = new ArrayList<>();
	
	private List<Cases> casesTestingSet = new ArrayList<>();
	
	@BeforeEach //once before every test
	void init() {
		
	    Authentication authentication = mock(Authentication.class);
	    SecurityContext securityContext = mock(SecurityContext.class);
	    
	    genToken = token.generateToken(SecurityContextHolder.getContext().getAuthentication());
	    
		employeeTestingSet = new ArrayList<>(); //clear to reset
		casesTestingSet = new ArrayList<>();
		
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
		
		casesTestingSet.add(case1);
		casesTestingSet.add(case2);
		casesTestingSet.add(case3);
		casesTestingSet.add(case4);
		casesTestingSet.add(case5);
		casesTestingSet.add(case6);
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getAllCases() throws Exception{
		Mockito.when(casesService.getAllCases()).thenReturn(casesTestingSet);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases")
												.contentType(MediaType.APPLICATION_JSON)
												.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(6)))
				.andExpect(content().json(mapper.writeValueAsString(casesTestingSet)));
		
		verify(casesService, times(1)).getAllCases();
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCasesByID() throws Exception{
		Mockito.when(casesService.getCaseByID(casesTestingSet.get(0).getId())).thenReturn(casesTestingSet.get(0));
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/id")
												.contentType(MediaType.APPLICATION_JSON)
												.param("id", casesTestingSet.get(0).getId().toString())
												.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isOk())
				.andExpect(content().json(mapper.writeValueAsString(casesTestingSet.get(0))));
		
		verify(casesService, times(1)).getCaseByID(casesTestingSet.get(0).getId());
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCasesByIDNullParams() throws Exception{
		Mockito.when(casesService.getCaseByID(null)).thenThrow(new InvalidParamsException());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/id")
												.contentType(MediaType.APPLICATION_JSON)
												.param("id", "")
												.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCasesByIDInvalidParams() throws Exception{
		Mockito.when(casesService.getCaseByID(55)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/id")
												.contentType(MediaType.APPLICATION_JSON)
												.param("id", "55")
												.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$", is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(casesService, times(1)).getCaseByID(55);
	}
	
	//Incase params are of bad type
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCasesByIDInvalidParamType() throws Exception{
		Mockito.when(casesService.getCaseByID(any(int.class))).thenThrow(new InvalidParamsException());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/id")
												.contentType(MediaType.APPLICATION_JSON)
												.param("id", "test")
												.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCasesByLastName() throws Exception{
		List<Cases> egLastName = new ArrayList<Cases>();
		egLastName.add(casesTestingSet.get(0));
		String egName = egLastName.get(0).getCustomer().getLastName();
		
		Mockito.when(casesService.getCasesByLastName(egName)).thenReturn(egLastName);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/lastname")
												.param("lastname", egName)
												.contentType(MediaType.APPLICATION_JSON)
												.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(content().json(mapper.writeValueAsString(egLastName)));
		
		verify(casesService,times(1)).getCasesByLastName(egName);
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCasesByLastNameEmptyList() throws Exception{
		Mockito.when(casesService.getCasesByLastName("aaaa")).thenReturn(new ArrayList<Cases>());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/lastname")
												.param("lastname", "aaaa")
												.contentType(MediaType.APPLICATION_JSON)
												.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)))
				.andExpect(content().json(mapper.writeValueAsString(new ArrayList<Cases>())));
		
		verify(casesService, times(1)).getCasesByLastName("aaaa");
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCasesByLastNameInvalidParamType() throws Exception{
		Mockito.when(casesService.getCasesByLastName(any(String.class))).thenThrow(new InvalidParamsException());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/lastname")
												.param("lastname", "55")
												.contentType(MediaType.APPLICATION_JSON)
												.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCasesByFirstName() throws Exception{
		List<Cases> egFirstName = new ArrayList<Cases>();
		egFirstName.add(casesTestingSet.get(0));
		String egName = egFirstName.get(0).getCustomer().getFirstName();
		
		Mockito.when(casesService.getCasesByFirstName(egName)).thenReturn(egFirstName);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/firstname")
											.param("firstname", egName)
											.contentType(MediaType.APPLICATION_JSON)
											.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(content().json(mapper.writeValueAsString(egFirstName)));
		
		verify(casesService,times(1)).getCasesByFirstName(egName);
	}

	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCasesByFirstNameEmptyList() throws Exception{
		Mockito.when(casesService.getCasesByFirstName("aaaa")).thenReturn(new ArrayList<Cases>());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/firstname")
											.param("firstname", "aaaa")
											.contentType(MediaType.APPLICATION_JSON)
											.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)))
				.andExpect(content().json(mapper.writeValueAsString(new ArrayList<Cases>())));
		
		verify(casesService, times(1)).getCasesByFirstName("aaaa");
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCasesByFirstNameInvalidParamType() throws Exception{
		Mockito.when(casesService.getCasesByFirstName(any(String.class))).thenThrow(new InvalidParamsException());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/firstname")
											.param("firstname", "55")
											.contentType(MediaType.APPLICATION_JSON)
											.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	//Principal-GET
	@Test
	@WithUserDetails(value="employee1", userDetailsServiceBeanName="testEmployeeDetails")
	void getAllEmployeeUserCases() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		Mockito.when(employeeService.getEmployeeByID(any(Integer.class))).thenReturn(emp);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/cases/users")
												.contentType(MediaType.APPLICATION_JSON)
												.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isOk())
				.andExpect(content().json(mapper.writeValueAsString(emp.getCases())));
		
		verify(employeeService, times(1)).getEmployeeByID(any(Integer.class));
	}

	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void saveNewCase() throws Exception{
		Cases egCase = casesTestingSet.get(0);
		egCase.setId(0);
		egCase.setProduct(new Products("New product", "Altered example product name here"));
		Mockito.when(casesService.saveNewCase(any(Cases.class), any(int.class), any(int.class))).thenReturn(egCase);
		
		String body = mapper.writeValueAsString(egCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/cases").param("empId","1")
																						.param("productId", "1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(body)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(body));
		
		verify(casesService, times(1)).saveNewCase(any(Cases.class), any(int.class), any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void saveNewCaseNullEmpID() throws Exception{
		Cases egCase = casesTestingSet.get(0);
		egCase.setId(0);
		egCase.setProduct(new Products("New product", "Altered example product name here"));
		Mockito.when(casesService.saveNewCase(any(Cases.class), any(int.class), any(int.class))).thenThrow(new InvalidParamsException());
		
		String body = mapper.writeValueAsString(egCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/cases").param("empId","")
																						.param("productId", "1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(body)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void saveNewCaseInvalidEmpID() throws Exception{
		Cases egCase = casesTestingSet.get(0);
		egCase.setId(0);
		egCase.setProduct(new Products("New product", "Altered example product name here"));
		Mockito.when(casesService.saveNewCase(any(Cases.class), any(int.class), any(int.class))).thenThrow(new EntityNotFound());
		
		String body = mapper.writeValueAsString(egCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/cases").param("empId","55")
																						.param("productId", "1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(body)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$", is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(casesService, times(1)).saveNewCase(any(Cases.class), any(int.class), any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void saveNewCaseInvalidEmpIDType() throws Exception{
		Cases egCase = casesTestingSet.get(0);
		egCase.setId(0);
		egCase.setProduct(new Products("New product", "Altered example product name here"));
		Mockito.when(casesService.saveNewCase(any(Cases.class), any(int.class), any(int.class))).thenThrow(new InvalidParamsException());
		
		String body = mapper.writeValueAsString(egCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/cases").param("empId","aaaa")
																						.param("productId", "1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(body)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	//Principal-Based POST
	
	@Test
	@WithUserDetails(value="employee1", userDetailsServiceBeanName="testEmployeeDetails")
	void saveNewUserCase() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		Cases egCase = casesTestingSet.get(2);
		egCase.setId(0);
		egCase.setProduct(new Products("New product", "Altered example product name here"));
		Mockito.when(employeeService.getEmployeeByID(any(Integer.class))).thenReturn(emp);
		String expected = mapper.writeValueAsString(egCase);
		Mockito.when(casesService.saveNewCase(any(Cases.class), any(int.class), any(Integer.class))).thenReturn(egCase);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/cases/users")
																	.param("productId", "1")
																	.contentType(MediaType.APPLICATION_JSON)
																	.content(expected)
																	.header("Authorization", "Bearer "+genToken)
																	.accept(MediaType.APPLICATION_JSON);
																	
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(expected));
		
		verify(employeeService, times(1)).getEmployeeByID(any(Integer.class));
		verify(casesService, times(1)).saveNewCase(any(Cases.class), any(int.class), any(Integer.class));
	}
	
	//PUT
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void updateCase() throws Exception{
		Cases egCase = casesTestingSet.get(0);
		egCase.setProduct(new Products("New product", "Altered example product name here"));
		Mockito.when(casesService.updateCase(any(Cases.class))).thenReturn(egCase);
		String body = mapper.writeValueAsString(egCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/cases")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(body)
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(content().json(body));
		
		verify(casesService, times(1)).updateCase(any(Cases.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void saveEmployeeToCase() throws Exception{
		Cases egCase = casesTestingSet.get(0);
		egCase.setProduct(new Products("New product", "Altered example product name here"));
		Mockito.when(casesService.saveEmployeeToCase(any(Cases.class), any(int.class))).thenReturn(egCase);
		String body = mapper.writeValueAsString(egCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/cases/id/employees").param("empId", "4")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(body)
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(content().json(body));
		
		verify(casesService, times(1)).saveEmployeeToCase(any(Cases.class), any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void saveEmployeeToCaseNullParams() throws Exception{
		Cases egCase = casesTestingSet.get(0);
		egCase.setProduct(new Products("New product", "Altered example product name here"));
		Mockito.when(casesService.saveEmployeeToCase(any(Cases.class), any(int.class))).thenThrow(new InvalidParamsException());
		String body = mapper.writeValueAsString(egCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/cases/id/employees").param("empId", "")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(body)
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
		
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void saveEmployeeToCaseInvalidParams() throws Exception{
		Cases egCase = casesTestingSet.get(0);
		egCase.setProduct(new Products("New product", "Altered example product name here"));
		Mockito.when(casesService.saveEmployeeToCase(any(Cases.class), any(int.class))).thenThrow(new EntityNotFound());
		String body = mapper.writeValueAsString(egCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/cases/id/employees").param("empId", "55")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(body)
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$", is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(casesService, times(1)).saveEmployeeToCase(any(Cases.class), any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void saveEmployeeToCaseInvalidParamsType() throws Exception{
		Cases egCase = casesTestingSet.get(0);
		egCase.setProduct(new Products("New product", "Altered example product name here"));
		Mockito.when(casesService.saveEmployeeToCase(any(Cases.class), any(int.class))).thenThrow(new InvalidParamsException());
		String body = mapper.writeValueAsString(egCase);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/cases/id/employees").param("empId", "aaaa")
																			.contentType(MediaType.APPLICATION_JSON)
																			.content(body)
																			.header("Authorization", "Bearer "+genToken)
																			.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	//Principal-Based PUT
	
	@Test
	@WithUserDetails(value="employee1", userDetailsServiceBeanName="testEmployeeDetails")
	void updateUserCases() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		Cases cases = emp.getCases().iterator().next();
		
		Mockito.when(employeeService.getEmployeeByID(any(Integer.class))).thenReturn(emp);
		String expected = mapper.writeValueAsString(cases);
		Mockito.when(casesService.updateCase(any(Cases.class), any(Employee.class))).thenReturn(cases);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/cases/users")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expected)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(content().json(expected));
		
		verify(employeeService, times(1)).getEmployeeByID(any(Integer.class));
		verify(casesService, times(1)).updateCase(any(Cases.class), any(Employee.class));
	}
	
	@Test
	@WithUserDetails(value="employee1", userDetailsServiceBeanName="testEmployeeDetails")
	void updateUserCasesNonMatchingBody() throws Exception{
		Employee emp = employeeTestingSet.get(0);
		Cases cases = emp.getCases().iterator().next();
		cases.setId(99);
		
		Mockito.when(employeeService.getEmployeeByID(any(Integer.class))).thenReturn(emp);
		String expected = mapper.writeValueAsString(cases);
		Mockito.when(casesService.updateCase(any(Cases.class), any(Employee.class))).thenReturn(null);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/cases/users")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expected)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
			.andExpect(status().isNotFound());
		
		verify(employeeService, times(1)).getEmployeeByID(any(Integer.class));
		verify(casesService, times(1)).updateCase(any(Cases.class), any(Employee.class));
	}
	
	//DELETE
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteCaseByID() throws Exception{
		Mockito.doNothing().when(casesService).deleteCaseByID(casesTestingSet.get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/cases/id").param("id", "1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNoContent());
		
		verify(casesService, times(1)).deleteCaseByID(1);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteCaseByIDNullID() throws Exception{
		Mockito.doNothing().when(casesService).deleteCaseByID(null);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/cases/id").param("id", "")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteCaseByIDInvalidID() throws Exception{
		Mockito.doThrow(new EntityNotFound()).when(casesService).deleteCaseByID(55);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/cases/id").param("id", "55")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$", is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(casesService, times(1)).deleteCaseByID(55);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteCaseByIDInvalidIDType() throws Exception{
		Mockito.doThrow(new EntityNotFound()).when(casesService).deleteCaseByID(any(int.class));
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/cases/id").param("id", "aaaa")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteEmployeeFromCase() throws Exception{
		Mockito.doNothing().when(casesService).deleteEmployeeFromCase(1,1);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/cases/id/employees").param("id", "1").param("empId","1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNoContent());
		
		verify(casesService, times(1)).deleteEmployeeFromCase(1,1);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles={"MANAGER"})
	void deleteCasesWithDiscontinuedProducts() throws Exception{
		Mockito.doNothing().when(casesService).deleteCasesWithDiscontinuedProducts(any(Integer.class));
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/cases/products/customers")
																			.param("productId", "1")
																			.contentType(MediaType.APPLICATION_JSON)
																			.header("Authorization", "Bearer "+genToken);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNoContent());
		
		verify(casesService, times(1)).deleteCasesWithDiscontinuedProducts(any(Integer.class));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteEmployeeFromCaseNullParams() throws Exception{
		Mockito.doNothing().when(casesService).deleteEmployeeFromCase(null, null);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/cases/id/employees").param("id", "").param("empId","")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteEmployeeFromCaseInvalidParams() throws Exception{
		Mockito.doThrow(new EntityNotFound()).when(casesService).deleteEmployeeFromCase(55, 55);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/cases/id/employees").param("id", "55").param("empId","55")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$", is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(casesService, times(1)).deleteEmployeeFromCase(55,55);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteEmployeeFromCaseInvalidParamsType() throws Exception{
		Mockito.doThrow(new EntityNotFound()).when(casesService).deleteEmployeeFromCase(any(int.class),any(int.class));
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/cases/id/employees").param("id", "aaaa").param("empId","aaaa")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
}
