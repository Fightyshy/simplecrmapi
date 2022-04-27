package com.simplecrmapi.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="customer")
public class Customer extends Person {
	
	@Column(name="pref_comms")
	private String prefComms;
	
	@Column(name="occupation")
	private String occupation;
	
	@Column(name="industry")
	private String industry;
	
	@OneToMany(cascade = {CascadeType.DETACH,CascadeType.REFRESH,CascadeType.REMOVE})
	@JoinColumn(name="address_id")
	private List<Address> addresses;
	
	@OneToOne(cascade = {CascadeType.DETACH,CascadeType.REFRESH,CascadeType.REMOVE})
	@JoinColumn(name="social_media_id")
	private SocialMedia socialMedia;
	
	public Customer(@NotNull @NotEmpty String firstName, String middleName, @NotNull @NotEmpty String lastName,
			@NotNull @NotEmpty LocalDate dateOfBirth, Integer phoneNumber, String emailAddress, String prefComms, String occupation, String industry) {
		super(firstName, middleName, lastName, dateOfBirth, phoneNumber, emailAddress);
		this.prefComms = prefComms;
		this.occupation = occupation;
		this.industry = industry;
	}


	public Customer() {
		this.addresses = new ArrayList<Address>();
	}


	public String getPrefComms() {
		return prefComms;
	}


	public void setPrefComms(String prefComms) {
		this.prefComms = prefComms;
	}


	public String getOccupation() {
		return occupation;
	}


	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}


	public String getIndustry() {
		return industry;
	}


	public void setIndustry(String industry) {
		this.industry = industry;
	}


	public List<Address> getAddresses() {
		return addresses;
	}


	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}


	public SocialMedia getSocialMedia() {
		return socialMedia;
	}


	public void setSocialMedia(SocialMedia socialMedia) {
		this.socialMedia = socialMedia;
	}
	
}
