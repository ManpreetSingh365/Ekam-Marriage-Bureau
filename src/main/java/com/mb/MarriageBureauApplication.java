package com.mb;

import java.awt.Image;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mb.config.AppConfig;
import com.mb.domain.USER_ROLE;
import com.mb.entities.User;
import com.mb.helpers.AppConstants;
import com.mb.repositories.UserRepo;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.GenerationType;

import com.mb.exception.GlobalExceptionHandler;

@SpringBootApplication
@EnableScheduling
public class MarriageBureauApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MarriageBureauApplication.class, args);
		System.out.println("<=====: This Spring Boot Website Developed by MANPREET SINGH (9592297120) :=====>");
	}

	@Bean
	public GlobalExceptionHandler globalExceptionHandler() {
		return new GlobalExceptionHandler();
	}

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder appPasswordEncoder;

	@Value("${admin.email}")
	private String adminEmail;

	@Value("mbuser@gmail.com")
	private String mbEmail;

	@Value("employee@gmail.com")
	private String empEmail;

	@PostConstruct
	public void createAdmin() throws Exception {
		User adminUser = new User();

		List<String> adminImagesList = new ArrayList<>();
		adminImagesList.add(
				"https://res.cloudinary.com/dcrlfty5k/image/upload/v1730366358/d459c434-00ea-4cff-9b7d-c7f18c7023e3.png");

		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm"));
		String currentDateAndTime = date + "_" + time;

		adminUser.setName("Admin");
		adminUser.setUserCreationTime(currentDateAndTime);
		adminUser.setEmail(adminEmail);
		adminUser.setPassword(appPasswordEncoder.encode("admin@321"));
//		adminUser.setRoleList(List.of("ADMIN"));
		adminUser.setImagesList(adminImagesList);
		adminUser.setGender("male");
		adminUser.setReligion("sikh");
		adminUser.setCaste("ramgarhia");
		adminUser.setDateOfBirth("16/11/2000");
		adminUser.setHeight(6.2);
		adminUser.setPlace("indian");
		adminUser.setMarriedStatus("Married");
		adminUser.setQualification("Under-Graduate");
		adminUser.setOccupation("business");
		adminUser.setFamilyStatus("moderate");
		adminUser.setTotalFamilyMembers(5);
		adminUser.setTotalBrothers(2);
		adminUser.setTotalSisters(1);
		adminUser.setPhoneNumber1("1234567890");
		adminUser.setFormFilledBy("Self");
		adminUser.setSubscriptionIsActive(true);
		adminUser.setRole(USER_ROLE.ROLE_ADMIN);

		userRepo.findByEmail(adminEmail).ifPresentOrElse(user1 -> {
		}, () -> {
			userRepo.save(adminUser);
			System.out.println("<==========: Admin has Created Succesfully :==========>");
		});
	}

	@PostConstruct
	public void createMbUser() throws Exception {
		User mbUser = new User();

		List<String> mbImagesList = new ArrayList<>();
		mbImagesList.add("https://res.cloudinary.com/dcrlfty5k/image/upload/v1732807056/mb_user_o8xtxu.webp");

		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm"));
		String currentDateAndTime = date + "_" + time;

		mbUser.setName("MB User");
		mbUser.setUserCreationTime(currentDateAndTime);
		mbUser.setEmail(mbEmail);
		mbUser.setPassword(appPasswordEncoder.encode("mbuser@321"));
		mbUser.setRoleList(List.of("USER"));
		mbUser.setImagesList(mbImagesList);
		mbUser.setGender("male");
		mbUser.setReligion("sikh");
		mbUser.setCaste("ramgarhia");
		mbUser.setSubcaste("Bhamra");
		mbUser.setDateOfBirth("16/11/2000");
		// mbUser.setBrithTime("5:50 PM");
		mbUser.setBrithTime("07:30");
		mbUser.setHeight(6.2);
		mbUser.setPlace("indian");
		mbUser.setMarriedStatus("married");
		mbUser.setQualification("under-graduate");
		mbUser.setOccupation("business");
		mbUser.setFamilyStatus("moderate");
		mbUser.setTotalFamilyMembers(5);
		mbUser.setTotalBrothers(2);
		mbUser.setTotalSisters(1);
		mbUser.setPhoneNumber1("1234567890");
		mbUser.setFormFilledBy("self");
		mbUser.setSubscriptionIsActive(true); 
		mbUser.setRole(USER_ROLE.ROLE_USER);

		userRepo.findByEmail(mbEmail).ifPresentOrElse(user1 -> {
		}, () -> {
			userRepo.save(mbUser);
			System.out.println("<==========: mbUser has Created Succesfully :==========>");
		});
	}
	
	@PostConstruct
	public void createEmployee() throws Exception {
		User empUser = new User();

		List<String> empImagesList = new ArrayList<>();
		empImagesList.add(
				"https://res.cloudinary.com/dcrlfty5k/image/upload/v1733046000/4044b1eb-6a1a-4443-b1c1-37bbf5c369e7_ka2g27.webp");

		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm"));
		String currentDateAndTime = date + "_" + time;

		empUser.setName("MB Employee");
		empUser.setUserCreationTime(currentDateAndTime);
		empUser.setEmail(empEmail); // employee@gmail.com
		empUser.setPassword(appPasswordEncoder.encode("emp@321"));
//		empUser.setRoleList(List.of("ADMIN"));
		empUser.setImagesList(empImagesList);
		empUser.setGender("male");
		empUser.setReligion("sikh");
		empUser.setCaste("ramgarhia");
		empUser.setDateOfBirth("16/11/2000");
		empUser.setHeight(6.2);
		empUser.setPlace("indian");
		empUser.setMarriedStatus("Married");
		empUser.setQualification("Under-Graduate");
		empUser.setOccupation("business");
		empUser.setFamilyStatus("moderate");
		empUser.setTotalFamilyMembers(5);
		empUser.setTotalBrothers(2);
		empUser.setTotalSisters(1);
		empUser.setPhoneNumber1("1234567890");
		empUser.setFormFilledBy("Self");
		empUser.setSubscriptionIsActive(true);
		empUser.setRole(USER_ROLE.ROLE_EMPLOYEE);

		userRepo.findByEmail(empEmail).ifPresentOrElse(user1 -> {
		}, () -> {
			userRepo.save(empUser);
			System.out.println("<==========: Employee has Created Succesfully :==========>");
		});
	}
}
