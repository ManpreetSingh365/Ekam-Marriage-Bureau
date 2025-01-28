package com.mb.forms;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.mb.entities.User;
import com.mb.validators.ValidFile;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserFormDetails {
	@Size(min = 3, message = "Username is Required")
	@Size(min = 8, message = "Name must be Atleast 3 characters long")
	private String yourName;
	@NotBlank(message = "Gender is required")
	private String gender;

	@NotBlank(message = "Religion is required")
	private String religion; // hindu, sikh, muslium
	@NotBlank(message = "Caste is required")
	private String caste; // ramgharia, suri, jatt

	private String subcaste; // Bhamra...

//	@NotBlank(message = "MinAge is required")
//	@Min(value = 19, message = "Please fill Min. & Max. Age as follow")
	private int minAge;
//	@NotNull(message = "Age is required")
//	@Range(min = 18, max = 60, message = "Age must be between 18 and 100")
//	private int age;
//	@NotBlank(message = "MaxAge is required")
//	@Max(value = 60, message = "Please fill Min. & Max. Age+ as follow")
	private int maxAge;

	@NotBlank(message = "dateOfBirth is required")
	private String dateOfBirth;
//	@NotNull(message = "Age is required")
//	@Range(min = 18, max = 60, message = "Age must be between 18 and 100")
	private Integer age;
	private String brithTime;

//	@NotBlank(message = "MinHeight is required")
//	@Min(value = 4, message = "Please fill Min. & Max. Height as follow")
	private double minHeight;
	@NotNull(message = "Height is required")
	@Range(min = 3, max = 8, message = "Height must be between 3 and 8")
	private Double height;
//	@NotBlank(message = "MaxHeight is required")
//	@Max(value = 7, message = "Please fill Min. & Max. Height+ as follow")
	private double maxHeight;

	// Created Annotation that will manage File Properties such as Validate, Size,
	// Resolution...
	@ValidFile(message = "Invalid File")
	private MultipartFile userImages;

	@NotBlank(message = "MarriedStatus is required")
	private String marriedStatus;
	@NotBlank(message = "Place is required")
	private String place;
	private String nriPlace;
	@NotBlank(message = "Qualification is required")
	private String qualification;
	private String qualificationField;
	@NotBlank(message = "Occupation is required")
	private String occupation;
	private String yourJobTitle;
	private String yourJobSalary;

	@NotBlank(message = "familyStatus is required")
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

	@NotBlank(message = "PhoneNumber1 is required")
	@Size(min = 8, max = 12, message = "Invalid Phone Number")
	private String phoneNumber1;
	private String phoneNumber2;

	@NotBlank(message = "formFilledBy is required")
	private String formFilledBy;

	@NotBlank(message = "Accept Terms & Conditions to Register")
	private String agreement;

//	@Getter(value = AccessLevel.NONE)
//	@AssertTrue(message = "Accept Terms & Conditions to Register")
//	private boolean isAgreemented = false;

}
