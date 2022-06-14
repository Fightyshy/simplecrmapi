package com.simplecrmapi.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Validator;

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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecrmapi.dao.CustomerDAO;
import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.service.CustomerService;
import com.simplecrmapi.test.util.CSVParser;
import com.simplecrmapi.util.ViewFormatter.CustomerController;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class CustomerControllerValidationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private Validator validator;
	
	@MockBean
	private CustomerDAO customerDAO;
	
	@MockBean
	private CustomerService customerService;
	
	@InjectMocks
	private CustomerController customerController;
	
	private List<Customer> finalTestingSet = new ArrayList<Customer>();
	
	@BeforeEach //once before every test
	void init() {
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
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerSuccess() throws Exception{
		Customer cus = finalTestingSet.get(1);
		cus.setFirstName("John");
		cus.setMiddleName("Sam");
		cus.setLastName("Smith");
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenReturn(cus);
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedBody));
		
		verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerFailed() throws Exception{
		Customer cus = finalTestingSet.get(1);
		cus.setFirstName("John123");
		cus.setMiddleName("Sam123"); //Validation error if field empty
		cus.setLastName("Smith123");
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
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
	void saveFullCustomerFailedDate() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setDateOfBirth(LocalDate.now());
		
		String body = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
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
	void saveFullCustomerFailedPhoneNumberType() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setPhoneNumber("aaaaaaaaaaaa");
		String body = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
																	.contentType(MediaType.APPLICATION_JSON)
																	.content(body)
																	.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Validation failed due to: [Input error in field phoneNumber with value aaaaaaaaaaaa. Please use numbers only]")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerFailedEmail() throws Exception{
		Customer cus =  finalTestingSet.get(1);
		cus.setEmailAddress("test");
		String body = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
																	.contentType(MediaType.APPLICATION_JSON)
																	.content(body)
																	.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Validation failed due to: [Input error in field emailAddress with value test. Please input a valid email address]")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerFailedOccupationAlphabet() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setOccupation("test123");
		String body = mapper.writeValueAsString(cus);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(body)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Validation failed due to: [Input error in field occupation with value test123. Only letters are allowed]")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerFailedIndustryAlphabet() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setIndustry("test123");
		String body = mapper.writeValueAsString(cus);
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(body)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Validation failed due to: [Input error in field industry with value test123. Only letters are allowed]")));
	}
}
