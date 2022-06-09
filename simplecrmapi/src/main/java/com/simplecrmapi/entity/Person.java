package com.simplecrmapi.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.simplecrmapi.validation.AlphabetOnly;

//For extension only
@MappedSuperclass //means this is NOT an table entity
//https://www.baeldung.com/hibernate-inheritance
public class Person {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
//	@NotNull
	private Integer id;
	
	@Column(name="first_name")
	@NotNull
	@NotEmpty
	@AlphabetOnly
	private String firstName;
	
	@Column(name="middle_name")
	@AlphabetOnly
	private String middleName;
	
	@Column(name="last_name")
	@NotNull
	@NotEmpty
	@AlphabetOnly
	private String lastName;
	
	@Column(name="date_of_birth")
	@NotNull
	@Past
	private LocalDate dateOfBirth; //use this instead of java.Date.util (depreciated)
	//sauce https://stackoverflow.com/questions/43039614/insert-fetch-java-time-localdate-objects-to-from-an-sql-database-such-as-h2
	
	@Column(name="phone_number")
	private String phoneNumber;
	
	@Column(name="email_address")
	private String emailAddress;
	
	public Person() {
		this.firstName = "";
		this.lastName = "";
		this.dateOfBirth = LocalDate.now();
	}

	public Person(@NotNull @NotEmpty String firstName, String middleName, @NotNull @NotEmpty String lastName,
			@NotNull @NotEmpty LocalDate dateOfBirth, String phoneNumber, String emailAddress) {
		super();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
}
