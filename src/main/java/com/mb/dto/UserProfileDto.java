package com.mb.dto;

import com.mb.entities.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
public class UserProfileDto {

	private Long userId;
	private String name;
	private String userCreationTime;
	private String gender;
	private String religion;
	private String caste;
	private String subcaste;
	private String dateOfBirth;
	private Integer age;
	private String birthTime;
	private Double height;
	private String marriedStatus;
	private String place;
	private String nriPlace;
	private String qualification;
	private String qualificationField;
	private String occupation;
	private String yourJobTitle;
	private String yourJobSalary;
	private String familyStatus;
	private Integer totalFamilyMembers;
	private Integer totalBrothers;
	private Integer totalSisters;
	private String fatherName;
	private String fatherOccupation;
	private String fatherJobTitle;
	private String fatherJobSalary;
	private String motherName;
	private String motherOccupation;
	private String motherJobTitle;
	private String motherJobSalary;
	private String anyDemand;
	private String anyRemarks;
	private String address;
	private String phoneNumber1;
	private String phoneNumber2;
	private String formFilledBy;
	private String role;

	public UserProfileDto() {
		super();
		// TODO Auto-generated constructor stub
	}

	 // Constructor to initialize from a User entity
    public UserProfileDto(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
        this.userCreationTime = user.getUserCreationTime();
        this.gender = user.getGender();
        this.religion = user.getReligion();
        this.caste = user.getCaste();
        this.subcaste = user.getSubcaste();
        this.dateOfBirth = user.getDateOfBirth();
        this.age = user.getAge();
        this.birthTime = user.getBrithTime();
        this.height = user.getHeight();
        this.marriedStatus = user.getMarriedStatus();
        this.place = user.getPlace();
        this.nriPlace = user.getNriPlace();
        this.qualification = user.getQualification();
        this.qualificationField = user.getQualificationField();
        this.occupation = user.getOccupation();
        this.yourJobTitle = user.getYourJobTitle();
        this.yourJobSalary = user.getYourJobSalary();
        this.familyStatus = user.getFamilyStatus();
        this.totalFamilyMembers = user.getTotalFamilyMembers();
        this.totalBrothers = user.getTotalBrothers();
        this.totalSisters = user.getTotalSisters();
        this.fatherName = user.getFatherName();
        this.fatherOccupation = user.getFatherOccupation();
        this.fatherJobTitle = user.getFatherJobTitle();
        this.fatherJobSalary = user.getFatherJobSalary();
        this.motherName = user.getMotherName();
        this.motherOccupation = user.getMotherOccupation();
        this.motherJobTitle = user.getMotherJobTitle();
        this.motherJobSalary = user.getMotherJobSalary();
        this.anyDemand = user.getAnyDemand();
        this.anyRemarks = user.getAnyRemarks();
        this.address = user.getAddress();
        this.phoneNumber1 = user.getPhoneNumber1();
        this.phoneNumber2 = user.getPhoneNumber2();
        this.formFilledBy = user.getFormFilledBy();
        this.role = user.getRoleAsString();
    }
}
