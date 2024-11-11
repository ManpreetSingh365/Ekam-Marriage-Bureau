package com.mb.helpers;

import org.springframework.stereotype.Component;

import com.mb.forms.UserFormDetails;

@Component
public class UserDefaultValues {

	// Helper method to set default values
	public void setDefaultValues(UserFormDetails userFormDetails) {

		// Convert String to Integer with default values
//		String height = formatDouble(userFormDetails.getHeight());
//		String yourJobSalary = formatInteger(userFormDetails.getYourJobSalary());
//		String totalFamilyMembers = formatInteger(userFormDetails.getTotalFamilyMembers());
//		String totalBrothers = formatInteger(userFormDetails.getTotalBrothers());
//		String totalSisters = formatInteger(userFormDetails.getTotalSisters());
//		String fatherJobSalary = formatInteger(userFormDetails.getFatherJobSalary());
//		String motherJobSalary = formatInteger(userFormDetails.getMotherJobSalary());

		if (userFormDetails.getYourName() == null || userFormDetails.getYourName().trim().isEmpty()) {
			userFormDetails.setYourName("Not Mention");
		}
		if (userFormDetails.getGender() == null || userFormDetails.getGender().trim().isEmpty()) {
			userFormDetails.setGender("Not Mention");
		}
		if (userFormDetails.getReligion() == null || userFormDetails.getReligion().trim().isEmpty()) {
			userFormDetails.setReligion("Not Mention");
		}
		if (userFormDetails.getCaste() == null || userFormDetails.getCaste().trim().isEmpty()) {
			userFormDetails.setCaste("Not Mention");
		}
		if (userFormDetails.getSubcaste() == null || userFormDetails.getSubcaste().trim().isEmpty()) {
			userFormDetails.setSubcaste("Not Mention");
		}
		if (userFormDetails.getDateOfBirth() == null || userFormDetails.getDateOfBirth().trim().isEmpty()) {
			userFormDetails.setDateOfBirth("Not Mention");
		}
		if (userFormDetails.getBrithTime() == null || userFormDetails.getBrithTime().trim().isEmpty()) {
			userFormDetails.setBrithTime("Not Mention");
		}
		if (userFormDetails.getHeight() == null) {
			userFormDetails.setHeight(0.0);
		}
		if (userFormDetails.getMarriedStatus() == null || userFormDetails.getMarriedStatus().trim().isEmpty()) {
			userFormDetails.setMarriedStatus("Not Mention");
		}
		if (userFormDetails.getPlace() == null || userFormDetails.getPlace().trim().isEmpty()) {
			userFormDetails.setPlace("Not Mention");
		}
		if (userFormDetails.getNriPlace() == null || userFormDetails.getNriPlace().trim().isEmpty()) {
			userFormDetails.setNriPlace("Not Mention");
		}
		if (userFormDetails.getQualification() == null || userFormDetails.getQualification().trim().isEmpty()) {
			userFormDetails.setQualification("Not Mention");
		}
		if (userFormDetails.getQualificationField() == null
				|| userFormDetails.getQualificationField().trim().isEmpty()) {
			userFormDetails.setQualificationField("Not Mention");
		}
		if (userFormDetails.getOccupation() == null || userFormDetails.getOccupation().trim().isEmpty()) {
			userFormDetails.setOccupation("Not Mention");
		}
		if (userFormDetails.getYourJobTitle() == null || userFormDetails.getYourJobTitle().trim().isEmpty()) {
			userFormDetails.setYourJobTitle("Not Mention");
		}
		if (userFormDetails.getYourJobSalary() == null) {
			userFormDetails.setYourJobSalary("Not Mention"); // Default value for int field
		}
		if (userFormDetails.getFamilyStatus() == null || userFormDetails.getFamilyStatus().trim().isEmpty()) {
			userFormDetails.setFamilyStatus("Not Mention");
		}
		if (userFormDetails.getTotalFamilyMembers() == null) {
			userFormDetails.setTotalFamilyMembers(0);
		}
		if (userFormDetails.getTotalBrothers() == null) {
			userFormDetails.setTotalBrothers(0);
		}
		if (userFormDetails.getTotalSisters() == null ) {
			userFormDetails.setTotalSisters(0);
		}
		if (userFormDetails.getFatherName() == null || userFormDetails.getFatherName().trim().isEmpty()) {
			userFormDetails.setFatherName("Not Mention");
		}
		if (userFormDetails.getFatherOccupation() == null || userFormDetails.getFatherOccupation().trim().isEmpty()) {
			userFormDetails.setFatherOccupation("Not Mention");
		}
		if (userFormDetails.getFatherJobTitle() == null || userFormDetails.getFatherJobTitle().trim().isEmpty()) {
			userFormDetails.setFatherJobTitle("Not Mention");
		}
		if (userFormDetails.getFatherJobSalary() == null) {
			userFormDetails.setFatherJobSalary("Not Mention");
		}
		if (userFormDetails.getMotherName() == null || userFormDetails.getMotherName().trim().isEmpty()) {
			userFormDetails.setMotherName("Not Mention");
		}
		if (userFormDetails.getMotherOccupation() == null || userFormDetails.getMotherOccupation().trim().isEmpty()) {
			userFormDetails.setMotherOccupation("Not Mention");
		}
		if (userFormDetails.getMotherJobTitle() == null || userFormDetails.getMotherJobTitle().trim().isEmpty()) {
			userFormDetails.setMotherJobTitle("Not Mention");
		}
		if (userFormDetails.getMotherJobSalary() == null ) {
			userFormDetails.setMotherJobSalary("Not Mention");
		}
		if (userFormDetails.getAnyDemand() == null || userFormDetails.getAnyDemand().trim().isEmpty()) {
			userFormDetails.setAnyDemand("Not Mention");
		}
		if (userFormDetails.getAnyRemarks() == null || userFormDetails.getAnyRemarks().trim().isEmpty()) {
			userFormDetails.setAnyRemarks("Not Mention");
		}
		if (userFormDetails.getAddress() == null || userFormDetails.getAddress().trim().isEmpty()) {
			userFormDetails.setAddress("Not Mention");
		}
		if (userFormDetails.getPhoneNumber1() == null || userFormDetails.getPhoneNumber1().trim().isEmpty()) {
			userFormDetails.setPhoneNumber1("Not Mention");
		}
		if (userFormDetails.getPhoneNumber2() == null || userFormDetails.getPhoneNumber2().trim().isEmpty()) {
			userFormDetails.setPhoneNumber2("Not Mention");
		}
		if (userFormDetails.getFormFilledBy() == null || userFormDetails.getFormFilledBy().trim().isEmpty()) {
			userFormDetails.setFormFilledBy("Not Mention");
		}

	}

//	private String formatDouble(Double double1) {
//		if (double1 != null) {
//			return double1.toString(); // Convert integer to string if it's not null
//		} else {
//			return "Not Mentioned"; // Return default string if integer is null
//		}
//	}
//
//	private String formatInteger(Integer integer) {
//		if (integer != null) {
//			return integer.toString(); // Convert integer to string if it's not null
//		} else {
//			return "Not Mentioned"; // Return default string if integer is null
//		}
//	}

}
