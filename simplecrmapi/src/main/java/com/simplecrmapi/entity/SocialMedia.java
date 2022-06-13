package com.simplecrmapi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.simplecrmapi.enums.SocialMediaNames;

@Entity
@Table(name="social_media")
public class SocialMedia {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;
	
	@Column(name="pref_sm")
	@NotNull
	private String preferredSocialMedia; //Incase prefcomms is social media, "None" for no socialMedia pref
	
	@Column(name="facebook")
	private String facebookHandle;
	
	@Column(name="twitter")
	private String twitterHandle;
	
	@Column(name="instagram")
	private String instagramHandle;
	
	@Column(name="line")
	private String lineHandle;
	
	@Column(name="whatsapp")
	private String whatsappHandle;
	
	//default values to none if construction w/o args as null is for errors
	public SocialMedia() {
		this.preferredSocialMedia=SocialMediaNames.NONE.toString();
		this.facebookHandle=SocialMediaNames.NONE.toString();
		this.twitterHandle=SocialMediaNames.NONE.toString();
		this.instagramHandle=SocialMediaNames.NONE.toString();
		this.lineHandle=SocialMediaNames.NONE.toString();
		this.whatsappHandle=SocialMediaNames.NONE.toString();
	}

	public SocialMedia(@NotNull String preferredSocialMedia, String facebookHandle, String twitterHandle,
			String instagramHandle, String lineHandle, String whatsappHandle) {
		this.preferredSocialMedia = preferredSocialMedia;
		this.facebookHandle = facebookHandle;
		this.twitterHandle = twitterHandle;
		this.instagramHandle = instagramHandle;
		this.lineHandle = lineHandle;
		this.whatsappHandle = whatsappHandle;
	}
	
	public SocialMedia(@NotNull String preferredSocialMedia) {
		this.preferredSocialMedia = preferredSocialMedia;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPreferredSocialMedia() {
		return preferredSocialMedia;
	}

	public void setPreferredSocialMedia(String preferredSocialMedia) {
		this.preferredSocialMedia = preferredSocialMedia;
	}

	public String getFacebookHandle() {
		return facebookHandle;
	}

	public void setFacebookHandle(String facebookHandle) {
		this.facebookHandle = facebookHandle;
	}

	public String getTwitterHandle() {
		return twitterHandle;
	}

	public void setTwitterHandle(String twitterHandle) {
		this.twitterHandle = twitterHandle;
	}

	public String getInstagramHandle() {
		return instagramHandle;
	}

	public void setInstagramHandle(String instagramHandle) {
		this.instagramHandle = instagramHandle;
	}

	public String getLineHandle() {
		return lineHandle;
	}

	public void setLineHandle(String lineHandle) {
		this.lineHandle = lineHandle;
	}

	public String getWhatsappHandle() {
		return whatsappHandle;
	}

	public void setWhatsappHandle(String whatsappHandle) {
		this.whatsappHandle = whatsappHandle;
	}
	
	public boolean validSMChecker(String input) {
		for(SocialMediaNames names:SocialMediaNames.values()) {
			if(names.toString().equals(input)) {
				return true;
			}
		}
		return false;
	}
}
