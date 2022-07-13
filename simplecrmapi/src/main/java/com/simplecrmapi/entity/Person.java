package com.simplecrmapi.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import com.simplecrmapi.validation.AlphabetOnly;
import com.simplecrmapi.validation.NumbersInStringOnly;

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
	@AlphabetOnly(message="Please use alphabetical characters only")
	private String firstName;
	
	@Column(name="middle_name")
	@AlphabetOnly(message="Please use alphabetical characters only")
	private String middleName;
	
	@Column(name="last_name")
	@NotNull
	@NotEmpty
	@AlphabetOnly(message="Please use alphabetical characters only")
	private String lastName;
	
	@Column(name="date_of_birth")
	@NotNull
	@Past(message="Date has to be in the past")
	private LocalDate dateOfBirth; //use this instead of java.Date.util (depreciated)
	//sauce https://stackoverflow.com/questions/43039614/insert-fetch-java-time-localdate-objects-to-from-an-sql-database-such-as-h2
	
	@Column(name="phone_number")
	@NumbersInStringOnly(message="Please use numbers only")
	private String phoneNumber;
	
	@Column(name="email_address")
	@Email(message="Please input a valid email address")
	@NotNull
	private String emailAddress;
	
	@OneToMany(mappedBy="employee",cascade= {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
	private List<Address> address;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="social_media_id")
	private SocialMedia socialMedia;
	
	public Person() {
		this.firstName = "";
		this.lastName = "";
		this.dateOfBirth = LocalDate.now();
	}

	public Person(Integer id, @NotNull @NotEmpty String firstName, String middleName,
			@NotNull @NotEmpty String lastName,
			@NotNull @Past(message = "Date has to be in the past") LocalDate dateOfBirth, String phoneNumber,
			@Email(message = "Please input a valid email address") String emailAddress) {
		super();
		this.id = id;
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

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public SocialMedia getSocialMedia() {
		return socialMedia;
	}

	public void setSocialMedia(SocialMedia socialMedia) {
		this.socialMedia = socialMedia;
	}
}
