package com.simplecrmapi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="address")
public class Address {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
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
	private String postcode;
	
	@Column(name="country")
	@NotNull
	@NotEmpty
	private String country;
	
	@Column(name="province")
	private String province;
	
	@Column(name="city")
	@NotNull
	@NotEmpty
	private String city;
	
	@Column(name="phone_number")
	@NotNull
	@NotEmpty
	private Integer phoneNumber;
	
	@Column(name="fax")
	private Integer faxNumber;

	public Address() {
	
	}

	public Address(@NotNull @NotEmpty String typeOfAddress, @NotNull @NotEmpty String line1, String line2, String line3,
			@NotNull @NotEmpty String postcode, @NotNull @NotEmpty String country, String province,
			@NotNull @NotEmpty String city, @NotNull @NotEmpty Integer phoneNumber, Integer faxNumber) {
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public Integer getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Integer phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Integer getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(Integer faxNumber) {
		this.faxNumber = faxNumber;
	}
	
}
