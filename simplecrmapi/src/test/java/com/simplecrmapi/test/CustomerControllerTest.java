package com.simplecrmapi.test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecrmapi.dao.CustomerDAO;
import com.simplecrmapi.dao.UserDAO;
import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.rest.CustomerController;
import com.simplecrmapi.service.CustomerService;
import com.simplecrmapi.service.EmployeeService;
import com.simplecrmapi.service.UserService;
import com.simplecrmapi.test.util.CSVParser;
import com.simplecrmapi.util.CustomerInvalidAddressException;
import com.simplecrmapi.util.CustomerInvalidObjectsException;
import com.simplecrmapi.util.CustomerInvalidSocialMediaException;
import com.simplecrmapi.util.EntityNotFound;
import com.simplecrmapi.util.JwtAuthenticationFilter;
import com.simplecrmapi.util.TokenProvider;
import com.simplecrmapi.util.UnauthorizedEntryPoint;

//https://stackabuse.com/guide-to-unit-testing-spring-boot-rest-apis/
//https://stackoverflow.com/questions/65520264/how-to-test-json-structure-in-spring-boot
@WebMvcTest(CustomerController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//https://www.baeldung.com/java-beforeall-afterall-non-static
@AutoConfigureMockMvc
class CustomerControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	private CustomerDAO customerDAO;
	
	@MockBean
	private CustomerService customerService;
	
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
	
	private String genToken;
	
	@InjectMocks
	private CustomerController customerController;
	
	private List<Customer> finalTestingSet = new ArrayList<Customer>();
	
//	@BeforeAll //once before all tests
	@BeforeEach //once before every test
	void init() {
	    Authentication authentication = mock(Authentication.class);
	    SecurityContext securityContext = mock(SecurityContext.class);
	    
	    genToken = token.generateToken(SecurityContextHolder.getContext().getAuthentication());
		
		finalTestingSet = new ArrayList<Customer>();
		//parse data into pojos, 5x each
		CSVParser parser = new CSVParser();
		
		List<String[]> customerFile = parser.readLine("Customer.txt");
		List<Customer> testCustomers = parser.customerParser(customerFile);
		System.out.println(testCustomers.toString());
		
		List<String[]> addressFile = parser.readLine("Address.txt");
		List<Address> testAddresses = parser.addressParser(addressFile);
		System.out.println(testAddresses.toString());
		
		List<String[]> socialMedia = parser.readLine("SocialMedia.txt");
		List<SocialMedia> testSocialMedia = parser.socialMediaParser(socialMedia);
		System.out.println(testSocialMedia.toString());
		
		//setup address hashsets
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
		
		//link pojos together
//		List<Customer> finalTestingSet = new ArrayList<Customer>();
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
		
		finalTestingSet.add(customer1);
		finalTestingSet.add(customer2);
		finalTestingSet.add(customer3);
		finalTestingSet.add(customer4);
		finalTestingSet.add(customer5);
	}
	
	//Working example
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	//https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-method.html
	void getCustomerList() throws Exception{
		Mockito.when(customerService.getCustomers()).thenReturn(finalTestingSet);
		
		mockMvc.perform(MockMvcRequestBuilders
						.get("/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer "+genToken))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", hasSize(5)))
						.andExpect(jsonPath("$[0].firstName", is("Myrtle")))
						.andExpect(jsonPath("$[1].firstName", is("George")))
						.andExpect(jsonPath("$[2].firstName", is("Fryderyka")))
						.andExpect(jsonPath("$[3].firstName", is("Elvira")))
						.andExpect(jsonPath("$[4].firstName", is("Alannah")));
		
		verify(customerService, times(1)).getCustomers();
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCustomerListEmpty() throws Exception{
		List<Customer> emptyList = new ArrayList<>();
		
		Mockito.when(customerService.getCustomers()).thenReturn(emptyList);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/customers").contentType(MediaType.APPLICATION_JSON)
																.header("Authorization", "Bearer "+genToken))
												.andExpect(status().isOk())
												.andExpect(content().json(mapper.writeValueAsString(emptyList)));
		
		verify(customerService, times(1)).getCustomers();
	}

	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCustomerByID() throws Exception{
		Mockito.when(customerService.getCustomerByID(finalTestingSet.get(0).getId())).thenReturn(finalTestingSet.get(0));
		
		mockMvc.perform(MockMvcRequestBuilders
						.get("/customers/id").param("id", "1")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer "+genToken))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", notNullValue()))
						.andExpect(jsonPath("$.firstName", is("Myrtle")));
		
		verify(customerService, times(1)).getCustomerByID(1);
	}

	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCustomerByIDNullIDParams() throws Exception{
		Mockito.when(customerService.getCustomerByID(null)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/customers/id").param("id", "")
																.contentType(MediaType.APPLICATION_JSON)
																.header("Authorization", "Bearer "+genToken))
		
												.andExpect(status().isBadRequest())
												.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCustomerByIDInvalidIDParams() throws Exception{
		Mockito.when(customerService.getCustomerByID(55)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/customers/id")
				.param("id", "55")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer "+genToken))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$", is("404 NOT FOUND: Entity not found due to invalid ID")));

		verify(customerService, times(1)).getCustomerByID(55);
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCustomerByLastName() throws Exception{
		List<Customer> dupeSet = new ArrayList<>();
		dupeSet.add(finalTestingSet.get(0));
		dupeSet.add(finalTestingSet.get(0));
		dupeSet.add(finalTestingSet.get(0));
		Mockito.when(customerService.getCustomerByLastName("Delvalle")).thenReturn(dupeSet);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/customers/lastname").param("lastname", "Delvalle").contentType(MediaType.APPLICATION_JSON))
												.andExpect(status().isOk())
												.andExpect(jsonPath("$", hasSize(3)));
		
		verify(customerService).getCustomerByLastName("Delvalle");
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void getCustomerByFirstName() throws Exception{
		List<Customer> dupeSet = new ArrayList<>();
		dupeSet.add(finalTestingSet.get(0));
		dupeSet.add(finalTestingSet.get(0));
		dupeSet.add(finalTestingSet.get(0));
		Mockito.when(customerService.getCustomerByLastName("Myrtle")).thenReturn(dupeSet);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/customers/firstname")
											.param("firstname", "Myrtle")
											.contentType(MediaType.APPLICATION_JSON)
											.header("Authorization", "Bearer "+genToken))
												.andExpect(status().isOk())
												.andExpect(jsonPath("$", hasSize(3)));
		
		verify(customerService).getCustomerByLastName("Myrtle");
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	//Combination of the two below
	//https://stackoverflow.com/questions/68194612/how-to-debug-the-error-java-lang-assertionerror-no-value-at-json-path-name
	//https://stackoverflow.com/questions/65520264/how-to-test-json-structure-in-spring-boot
	void saveFullCustomerOneAddressYesSM() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setId(0);
		cus.setFirstName("John");
		cus.setMiddleName("Sam");
		cus.setLastName("Smith");

		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenReturn(cus);
		
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.header("Authorization", "Bearer "+genToken)
													.accept(MediaType.APPLICATION_JSON);
		
        mockMvc.perform(mockRequest)
		        .andExpect(status().isCreated())
		        .andExpect(content().json(expectedBody));
        
        verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void saveFullCustomerOnePlusAddressYesSM() throws Exception{
		Customer cus = finalTestingSet.get(1);
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenReturn(cus);
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.header("Authorization", "Bearer "+genToken)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedBody));
		
		verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	//Assume working for 1 or more addresses
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void saveFullCustomerOneAddressNoSM() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setSocialMedia(null);
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenThrow(new CustomerInvalidSocialMediaException());
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.header("Authorization", "Bearer "+genToken)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$", is("406 NOT ACCEPTABLE: Customer is missing non-null social media object")));
		
		verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void saveFullCustomerNoAddressYesSMEmptyList() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setAddress(new ArrayList<Address>());
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenThrow(new CustomerInvalidAddressException());
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.header("Authorization", "Bearer "+genToken)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$", is("406 NOT ACCEPTABLE: Customer is missing non-null/empty address object(s)")));
		
		verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void saveFullCustomerNoAddressYesSMNullList() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setAddress(null);
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenThrow(new CustomerInvalidAddressException());
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.header("Authorization", "Bearer "+genToken)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$", is("406 NOT ACCEPTABLE: Customer is missing non-null/empty address object(s)")));
		
		verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void saveFullCustomerNoAddressNoSM() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setAddress(null);
		cus.setSocialMedia(null);
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenThrow(new CustomerInvalidObjectsException());
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.header("Authorization", "Bearer "+genToken)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$", is("406 NOT ACCEPTABLE: Customer is missing non-null/empty social media and address object(s)")));
		
		verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void saveAddressOnlyToCustomer() throws Exception{
		Address add = finalTestingSet.get(3).getAddress().get(0);
		
		Mockito.when(customerService.saveAddressToCustomer(any(Address.class), any(int.class))).thenReturn(add);
		String expectedBody = mapper.writeValueAsString(add);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers/id/addresses",add).param("id", "0")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedBody));
		
		verify(customerService).saveAddressToCustomer(any(Address.class), any(int.class));
	}

	//Probably not needed
//	@Test
//	@WithMockUser(username="john", roles= {"CUSTOMER"})
//	void saveSocialMediaOnlyToCustomer() {
//		
//	}

	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void updateAddressOnlyToCustomer() throws Exception{
		Address add = finalTestingSet.get(1).getAddress().get(0);
		add.setCity("Very cursed update here");
		
		Mockito.when(customerService.updateCustomerAddressByID(any(Address.class))).thenReturn(add);
		String expectedBody = mapper.writeValueAsString(add);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers/id/addresses",add)
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(expectedBody));
		
		verify(customerService).updateCustomerAddressByID(any(Address.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void updateSocialMediaOnlyToCustomer() throws Exception{
		SocialMedia sm = finalTestingSet.get(1).getSocialMedia();
		sm.setPreferredSocialMedia("NO_PREFERENCE");
		
		Mockito.when(customerService.updateCustomerSocialMedia(any(SocialMedia.class), any(int.class))).thenReturn(sm);
		String expectedBody = mapper.writeValueAsString(sm);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers/id/socialmedia",sm).param("id", finalTestingSet.get(1).getId().toString())
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(expectedBody));
		
		verify(customerService).updateCustomerSocialMedia(any(SocialMedia.class), any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void updateSocialMediaOnlyToCustomerInvalidID() throws Exception{
		SocialMedia sm = finalTestingSet.get(1).getSocialMedia();
		sm.setPreferredSocialMedia("NO_PREFERENCE");
		
		Mockito.when(customerService.updateCustomerSocialMedia(any(SocialMedia.class), any(int.class))).thenThrow(new EntityNotFound());
		String expectedBody = mapper.writeValueAsString(sm);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers/id/socialmedia",sm).param("id", "55")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$",is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(customerService).updateCustomerSocialMedia(any(SocialMedia.class), any(int.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void updateSocialMediaOnlyToCustomerNullID() throws Exception{
		SocialMedia sm = finalTestingSet.get(1).getSocialMedia();
		sm.setPreferredSocialMedia("NO_PREFERENCE");
		
		Mockito.when(customerService.updateCustomerSocialMedia(any(SocialMedia.class), any(int.class))).thenThrow(new EntityNotFound());
		String expectedBody = mapper.writeValueAsString(sm);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers/id/socialmedia",sm).param("id", "")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.header("Authorization", "Bearer "+genToken)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void updateFullCustomer() throws Exception{
		Customer cus = finalTestingSet.get(0);
		System.out.println(cus.getId());
		cus.setFirstName("John");
		cus.setMiddleName("Sam");
		cus.setLastName("Smith");
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenReturn(cus);
		
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.header("Authorization", "Bearer "+genToken)
													.accept(MediaType.APPLICATION_JSON);
		
        mockMvc.perform(mockRequest)
        .andExpect(status().isOk())
        .andExpect(content().json(expectedBody))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.firstName", is("John")))
        .andExpect(jsonPath("$.middleName", is("Sam")))
        .andExpect(jsonPath("$.lastName", is("Smith")));
        
        verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void updateFullCustomerNullID() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setId(null);
		cus.setFirstName("John");
		cus.setMiddleName("Sam");
		cus.setLastName("Smith");
		
		//Why was this easier than it was https://stackoverflow.com/questions/40914401/unit-testing-spring-rest-controller-error-handling
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenThrow(new EntityNotFound());
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers",cus)
				.contentType(MediaType.APPLICATION_JSON)
				.content(expectedBody)
				.header("Authorization", "Bearer "+genToken)
				.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$",is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	@Test
	@WithMockUser(username="employee1", password="test123", roles= {"EMPLOYEE"})
	void updateFullCustomerInvalidID() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setId(55);
		cus.setFirstName("John");
		cus.setMiddleName("Sam");
		cus.setLastName("Smith");
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenThrow(new EntityNotFound());
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers",cus)
				.contentType(MediaType.APPLICATION_JSON)
				.content(expectedBody)
				.header("Authorization", "Bearer "+genToken)
				.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$",is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	//TODO move validation to seperate controller for clutter
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	//https://medium.com/swlh/https-medium-com-jet-cabral-testing-spring-boot-restful-apis-b84ea031973d
	void deleteCustomerByID() throws Exception{
		Mockito.doNothing().when(customerService).deleteCustomerByID(finalTestingSet.get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/customers/id").param("id", "1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNoContent());
		
		verify(customerService, times(1)).deleteCustomerByID(1);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteCustomerByIDInvalidID() throws Exception{
		Mockito.doThrow(new EntityNotFound()).when(customerService).deleteCustomerByID(55);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/customers/id").param("id", "55")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$",is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(customerService, times(1)).deleteCustomerByID(55);
	}
	
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void deleteCustomerAddressByIDs() throws Exception{
		Mockito.doNothing().when(customerService).deleteCustomerAddressByID(finalTestingSet.get(1).getId(),finalTestingSet.get(1).getAddress().get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/customers/id/addresses").param("customerid", "2").param("addressid", "1")
																			.contentType(MediaType.APPLICATION_JSON)
																			.header("Authorization", "Bearer "+genToken);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNoContent());
		
		verify(customerService, times(1)).deleteCustomerAddressByID(2, 1);
	}
	
	//Not delete, but overwrite old object with new empty object and NONE
	@Test
	@WithMockUser(username="employee2", password="test123", roles= {"MANAGER"})
	void wipeCustomerSocialMedia() throws Exception{
		Mockito.doNothing().when(customerService).deleteCustomerSocialMediaByID(finalTestingSet.get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/customers/id/socialmedia")
																		.param("id", "1")
																		.contentType(MediaType.APPLICATION_JSON)
																		.header("Authorization", "Bearer "+genToken);
		
		mockMvc.perform(mockRequest)
		.andDo(print())
		.andExpect(status().isNoContent());
		
		verify(customerService, times(1)).deleteCustomerSocialMediaByID(1);
	}
}
