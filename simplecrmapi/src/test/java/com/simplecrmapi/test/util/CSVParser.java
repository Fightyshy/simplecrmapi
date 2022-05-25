package com.simplecrmapi.test.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.simplecrmapi.entity.Address;
import com.simplecrmapi.entity.Customer;
import com.simplecrmapi.entity.Employee;
import com.simplecrmapi.entity.SocialMedia;
import com.simplecrmapi.entity.Cases;

public class CSVParser {
	
	public List<String[]> readLine(String filename){
		try {
		List<String[]> csvMap = new ArrayList<String[]>();
		
		FileInputStream file = new FileInputStream(filename);
		Scanner sc = new Scanner(file);
		
		while(sc.hasNextLine()) {
			String[] splitted = sc.nextLine().split(",");
			if(filename.equals("Address.txt")&&splitted.length==10) {
				String[] missingSplitted = new String[11];
				for(int i=0;i<splitted.length;i++) {
					missingSplitted[i]=splitted[i];
				}
				missingSplitted[10]=null;
				System.out.println("CSV line length: "+missingSplitted.length);
				csvMap.add(missingSplitted);
			}else if(filename.equals("SocialMedia.txt")&&splitted.length!=7) {
				String[] missingSM = new String[7];
				for(int j=0;j<splitted.length;j++) {
					missingSM[j] = splitted[j];
				}
				for(int y = splitted.length;y<missingSM.length;y++) {
					missingSM[y]="";
				}
				csvMap.add(missingSM);
			}
			
			else {
				csvMap.add(splitted);
			}
		}
		sc.close();
		return csvMap;
		}catch(IOException e) {
			System.out.println(e);
			return null;
		}
	}
	
	public List<Customer> customerParser(List<String[]> csvMap){
		List<Customer> customerOut = new ArrayList<Customer>();
		
		for(String[] csv:csvMap) {
			Customer test1 = new Customer();
			test1.setId(Integer.parseInt(csv[0]));
			test1.setFirstName(csv[1]);
			test1.setMiddleName(csv[2]);
			test1.setLastName(csv[3]);
			test1.setDateOfBirth(LocalDate.parse(csv[4]));
			test1.setPhoneNumber(csv[5]);
			test1.setEmailAddress(csv[6]);
			test1.setPrefComms(csv[7]);
			test1.setOccupation(csv[8]);
			test1.setIndustry(csv[9]);
			customerOut.add(test1);
		}
		
		return customerOut;
	}
	
	public List<Employee> employeeParser(List<String[]> csvMap){
		List<Employee> employeeOut = new ArrayList<Employee>();
		for(String[] csv:csvMap) {
			Employee test1 = new Employee();
			test1.setId(Integer.parseInt(csv[0]));
			test1.setFirstName(csv[1]);
			test1.setMiddleName(csv[2]);
			test1.setLastName(csv[3]);
			test1.setDateOfBirth(LocalDate.parse(csv[4]));
			test1.setPhoneNumber(csv[5]);
			test1.setEmailAddress(csv[6]);
			test1.setCasesActive(Integer.parseInt(csv[7]));
			test1.setCasesPending(Integer.parseInt(csv[8]));
			test1.setCasesResolved(Integer.parseInt(csv[9]));
			test1.setCasesClosed(Integer.parseInt(csv[10]));
			employeeOut.add(test1);
		}
		return employeeOut;
	}
	
	public List<Address> addressParser(List<String[]> csvMap){
		List<Address> addressOut = new ArrayList<Address>();
		
		for(String[] csv:csvMap) {
			Address address1 = new Address();
			address1.setId(Integer.parseInt(csv[0]));
			address1.setTypeOfAddress(csv[1]);
			address1.setLine1(csv[2]);
			address1.setLine2(csv[3]);
			address1.setLine3(csv[4]);
			address1.setPostcode(csv[5]);
			address1.setCountry(csv[6]);
			address1.setProvince(csv[7]);
			address1.setCity(csv[8]);
			address1.setPhoneNumber(csv[9]);
			address1.setFaxNumber(csv[10]);
			addressOut.add(address1);
		}
		return addressOut;
	}
	
	public List<SocialMedia> socialMediaParser(List<String[]> csvMap){
		List<SocialMedia> smOut = new ArrayList<SocialMedia>();
		
		for(String[] csv:csvMap) {
			SocialMedia socialMedia1 = new SocialMedia();
			socialMedia1.setId(Integer.parseInt(csv[0]));
			socialMedia1.setPreferredSocialMedia(csv[1]);
			socialMedia1.setFacebookHandle(csv[2]);
			socialMedia1.setTwitterHandle(csv[3]);
			socialMedia1.setInstagramHandle(csv[4]);
			socialMedia1.setLineHandle(csv[5]);
			socialMedia1.setWhatsappHandle(csv[6]);
			smOut.add(socialMedia1);
		}
		return smOut;
	}
	
	public List<Cases> caseParser(List<String[]> csvMap){
		List<Cases> caseOut = new ArrayList<Cases>();
		
		for(String [] csv:csvMap) {
			Cases cases1 = new Cases();
			cases1.setId(Integer.parseInt(csv[0]));
			cases1.setCasesStatus(csv[1]);
			cases1.setStartDate(LocalDateTime.parse(csv[2]));
			if(csv[3].equals("")) {
				cases1.setEndDate(null);
			}else {				
				cases1.setEndDate(LocalDateTime.parse(csv[3]));
			}
			cases1.setProduct(csv[4]);
			caseOut.add(cases1);
		}
		return caseOut;
	}
}
