package com.simplecrmapi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="social_media")
public class SocialMedia {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(name="pref_sm")
	@NotNull
	private String preferredSocialMedia; //Incase prefcomms is social media, "None" for no socialMedia pref
	
	//... include social media
	
	public SocialMedia() {
		this.preferredSocialMedia="None"; //Default value if constructed, since cannot be null;
	}
}
