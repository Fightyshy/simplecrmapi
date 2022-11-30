package com.simplecrmapi.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simplecrmapi.validation.AlphabetOnly;
import com.simplecrmapi.validation.Alphanumeric;
import com.simplecrmapi.validation.NumbersInStringOnly;

@Entity
@Table(name="address")
public class Address {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="type_of_address")
	@NotNull
	@NotEmpty
	private String typeOfAddress;
	
	@Column(name="line1")
	@NotNull
	@NotEmpty
	private String line1;
	
	@Column(name="line2")
	private String line2;
	
	@Column(name="line3")
	private String line3;
	
	@Column(name="postcode")
	@NotNull
	@NotEmpty
	@Alphanumeric
	private String postcode;
	
	@Column(name="country")
	@NotNull
	@NotEmpty
	@AlphabetOnly
	private String country;
	
	@Column(name="province")
	private String province;
	
	@Column(name="city")
	@NotNull
	@NotEmpty
	private String city;
	
	@Column(name="phone_number")
	@NotNull
	@NumbersInStringOnly
	private String phoneNumber;
	
	@Column(name="fax")
	@NumbersInStringOnly
	private String faxNumber;
	
	//TODO elegant solution, unidrectional required
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH,CascadeType.REFRESH})
	@JoinColumn(name="customer_id")
	@JsonIgnore
	private Customer customer;
	
	@ManyToOne(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH,CascadeType.REFRESH})
	@JoinColumn(name="employee_id")
	@JsonIgnore
	private Employee employee;

	public Address() {
	
	}

	public Address(@NotNull @NotEmpty String typeOfAddress, @NotNull @NotEmpty String line1, String line2, String line3,
			@NotNull @NotEmpty String postcode, @NotNull @NotEmpty String country, String province,
			@NotNull @NotEmpty String city, @NotNull @NotEmpty String phoneNumber, String faxNumber) {
		super();
		this.typeOfAddress = typeOfAddress;
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
		this.postcode = postcode;
		this.country = country;
		this.province = province;
		this.city = city;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTypeOfAddress() {
		return typeOfAddress;
	}

	public void setTypeOfAddress(String typeOfAddress) {
		this.typeOfAddress = typeOfAddress;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
}
