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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
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
import com.simplecrmapi.dao.CustomerDAO;
import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.rest.CustomerController;
import com.simplecrmapi.service.CustomerService;
import com.simplecrmapi.test.util.CSVParser;
import com.simplecrmapi.util.CustomerInvalidAddressException;
import com.simplecrmapi.util.CustomerInvalidObjectsException;
import com.simplecrmapi.util.CustomerInvalidSocialMediaException;
import com.simplecrmapi.util.EntityNotFound;

//https://stackabuse.com/guide-to-unit-testing-spring-boot-rest-apis/
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
	
	@InjectMocks
	private CustomerController customerController;
	
	private List<Customer> finalTestingSet = new ArrayList<Customer>();
	
	@BeforeAll
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
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	//https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/test-method.html
	void getCustomerList() throws Exception{
		Mockito.when(customerService.getCustomers()).thenReturn(finalTestingSet);
		
		mockMvc.perform(MockMvcRequestBuilders
						.get("/customers")
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", hasSize(5)))
						.andExpect(jsonPath("$[0].firstName", is("Myrtle")))
						.andExpect(jsonPath("$[1].firstName", is("George")))
						.andExpect(jsonPath("$[2].firstName", is("Fryderyka")))
						.andExpect(jsonPath("$[3].firstName", is("Elvira")))
						.andExpect(jsonPath("$[4].firstName", is("Alannah")));
		
		verify(customerService).getCustomers();
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getCustomerListEmpty() throws Exception{
		List<Customer> emptyList = new ArrayList<>();
		
		Mockito.when(customerService.getCustomers()).thenReturn(emptyList);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/customers").contentType(MediaType.APPLICATION_JSON))
												.andExpect(status().isOk())
												.andExpect(content().json(mapper.writeValueAsString(emptyList)));
	}

	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getCustomerByID() throws Exception{
		Mockito.when(customerService.getCustomerByID(finalTestingSet.get(0).getId())).thenReturn(finalTestingSet.get(0));
		
		mockMvc.perform(MockMvcRequestBuilders
						.get("/customers/id").param("id", "1")
						.contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", notNullValue()))
						.andExpect(jsonPath("$.firstName", is("Myrtle")));
		
//		verify(customerService).getCustomerByID(finalTestingSet.get(0).getId());
	}

	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getCustomerByIDNullIDParams() throws Exception{
		Mockito.when(customerService.getCustomerByID(null)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders.get("/customers/id").param("id", "").contentType(MediaType.APPLICATION_JSON))
		
												.andExpect(status().isBadRequest())
												.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getCustomerByIDInvalidIDParams() throws Exception{
		Mockito.when(customerService.getCustomerByID(55)).thenThrow(new EntityNotFound());
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/customers/id")
				.param("id", "55")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$", is("404 NOT FOUND: Entity not found due to invalid ID")));

//		verify(customerService).getCustomerByID(finalTestingSet.get(0).getId());
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
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
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void getCustomerByFirstName() throws Exception{
		List<Customer> dupeSet = new ArrayList<>();
		dupeSet.add(finalTestingSet.get(0));
		dupeSet.add(finalTestingSet.get(0));
		dupeSet.add(finalTestingSet.get(0));
		Mockito.when(customerService.getCustomerByLastName("Myrtle")).thenReturn(dupeSet);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/customers/firstname").param("firstname", "Myrtle").contentType(MediaType.APPLICATION_JSON))
												.andExpect(status().isOk())
												.andExpect(jsonPath("$", hasSize(3)));
		
		verify(customerService).getCustomerByLastName("Myrtle");
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
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
													.accept(MediaType.APPLICATION_JSON);
		
        mockMvc.perform(mockRequest)
		        .andExpect(status().isCreated())
		        .andExpect(content().json(expectedBody));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerOnePlusAddressYesSM() throws Exception{
		Customer cus = finalTestingSet.get(1);
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenReturn(cus);
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedBody));
	}
	
	//Assume working for 1 or more addresses
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerOneAddressNoSM() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setSocialMedia(null);
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenThrow(new CustomerInvalidSocialMediaException());
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$", is("406 NOT ACCEPTABLE: Customer is missing non-null social media object")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerNoAddressYesSMEmptyList() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setAddress(new ArrayList<Address>());
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenThrow(new CustomerInvalidAddressException());
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$", is("406 NOT ACCEPTABLE: Customer is missing non-null/empty address object(s)")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerNoAddressYesSMNullList() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setAddress(null);
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenThrow(new CustomerInvalidAddressException());
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$", is("406 NOT ACCEPTABLE: Customer is missing non-null/empty address object(s)")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveFullCustomerNoAddressNoSM() throws Exception{
		Customer cus = finalTestingSet.get(0);
		cus.setAddress(null);
		cus.setSocialMedia(null);
		
		Mockito.when(customerService.saveCustomerDetails(any(Customer.class))).thenThrow(new CustomerInvalidObjectsException());
		String expectedBody = mapper.writeValueAsString(cus);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers")
													.contentType(MediaType.APPLICATION_JSON)
													.content(expectedBody)
													.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andExpect(status().isNotAcceptable())
				.andExpect(jsonPath("$", is("406 NOT ACCEPTABLE: Customer is missing non-null/empty social media and address object(s)")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void saveAddressOnlyToCustomer() throws Exception{
		Address add = finalTestingSet.get(3).getAddress().get(0);
		
		Mockito.when(customerService.saveAddressToCustomer(any(Address.class), any(int.class))).thenReturn(add);
		String expectedBody = mapper.writeValueAsString(add);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/customers/addresses",add).param("id", "0")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(content().json(expectedBody));
	}

	//Probably not needed
//	@Test
//	@WithMockUser(username="john", roles= {"CUSTOMER"})
//	void saveSocialMediaOnlyToCustomer() {
//		
//	}

	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateAddressOnlyToCustomer() throws Exception{
		init();
		Address add = finalTestingSet.get(1).getAddress().get(0);
		add.setCity("Very cursed update here");
		
		Mockito.when(customerService.updateCustomerAddressByID(any(Address.class))).thenReturn(add);
		String expectedBody = mapper.writeValueAsString(add);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers/addresses",add)
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(expectedBody));
		
		verify(customerService).updateCustomerAddressByID(any(Address.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateSocialMediaOnlyToCustomer() throws Exception{
		init(); //works but weird
		SocialMedia sm = finalTestingSet.get(1).getSocialMedia();
		sm.setPreferredSocialMedia("NO_PREFERENCE");
		
		Mockito.when(customerService.updateCustomerSocialMedia(any(SocialMedia.class), any(int.class))).thenReturn(sm);
		String expectedBody = mapper.writeValueAsString(sm);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers/socialmedia",sm).param("id", finalTestingSet.get(1).getId().toString())
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(expectedBody));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateSocialMediaOnlyToCustomerInvalidID() throws Exception{
		init(); //works but weird
		SocialMedia sm = finalTestingSet.get(1).getSocialMedia();
		sm.setPreferredSocialMedia("NO_PREFERENCE");
		
		Mockito.when(customerService.updateCustomerSocialMedia(any(SocialMedia.class), any(int.class))).thenThrow(new EntityNotFound());
		String expectedBody = mapper.writeValueAsString(sm);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers/socialmedia",sm).param("id", "55")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$",is("404 NOT FOUND: Entity not found due to invalid ID")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void updateSocialMediaOnlyToCustomerNullID() throws Exception{
		init(); //works but weird
		SocialMedia sm = finalTestingSet.get(1).getSocialMedia();
		sm.setPreferredSocialMedia("NO_PREFERENCE");
		
		Mockito.when(customerService.updateCustomerSocialMedia(any(SocialMedia.class), any(int.class))).thenThrow(new EntityNotFound());
		String expectedBody = mapper.writeValueAsString(sm);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/customers/socialmedia",sm).param("id", "")
																		.contentType(MediaType.APPLICATION_JSON)
																		.content(expectedBody)
																		.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", is("400 BAD REQUEST: Invalid params")));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
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
	@WithMockUser(username="john", roles= {"CUSTOMER"})
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
				.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$",is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
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
				.accept(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$",is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(customerService).saveCustomerDetails(any(Customer.class));
	}
	
	//TODO move validation to seperate controller for clutter
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	//https://medium.com/swlh/https-medium-com-jet-cabral-testing-spring-boot-restful-apis-b84ea031973d
	void deleteCustomerByID() throws Exception{
		Mockito.doNothing().when(customerService).deleteCustomerByID(finalTestingSet.get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/customers/id").param("id", "1")
																		.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNoContent());
		
		verify(customerService, times(1)).deleteCustomerByID(1);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void deleteCustomerByIDInvalidID() throws Exception{
		Mockito.doThrow(new EntityNotFound()).when(customerService).deleteCustomerByID(55);
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/customers/id").param("id", "55")
																		.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$",is("404 NOT FOUND: Entity not found due to invalid ID")));
		
		verify(customerService, times(1)).deleteCustomerByID(55);
	}
	
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void deleteCustomerAddressByIDs() throws Exception{
		Mockito.doNothing().when(customerService).deleteCustomerAddressByID(finalTestingSet.get(1).getId(),finalTestingSet.get(1).getAddress().get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/customers/addresses").param("customerid", "2").param("addressid", "1")
																			.contentType(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
				.andDo(print())
				.andExpect(status().isNoContent());
		
		verify(customerService, times(1)).deleteCustomerAddressByID(2, 1);
	}
	
	//Not delete, but overwrite old object with new empty object and NONE
	@Test
	@WithMockUser(username="john", roles= {"CUSTOMER"})
	void wipeCustomerSocialMedia() throws Exception{
		Mockito.doNothing().when(customerService).deleteCustomerSocialMediaByID(finalTestingSet.get(0).getId());
		
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/customers/socialmedia").param("id", "1")
				.contentType(MediaType.APPLICATION_JSON);
		
		mockMvc.perform(mockRequest)
		.andDo(print())
		.andExpect(status().isNoContent());
		
		verify(customerService, times(1)).deleteCustomerSocialMediaByID(1);
	}
}
