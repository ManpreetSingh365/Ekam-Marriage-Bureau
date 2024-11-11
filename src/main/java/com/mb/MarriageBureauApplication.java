package com.mb;

import java.awt.Image;
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

	@PostConstruct
	public void createAdmin() throws Exception {
		User user = new User();

		List<String> imagesList = new ArrayList<>(); 
		imagesList.add("https://res.cloudinary.com/dcrlfty5k/image/upload/v1730366358/d459c434-00ea-4cff-9b7d-c7f18c7023e3.png");

		user.setName("Admin");
		user.setEmail(adminEmail);
		user.setPassword(appPasswordEncoder.encode("open"));
		user.setRoleList(List.of("ADMIN"));
        user.setImagesList(imagesList);
     
		user.setGender("male");
		user.setReligion("sikh");
		user.setCaste("ramgharia");
		user.setDateOfBirth("16/11/2000");
		user.setHeight(6.2);
		user.setPlace("indian");
		user.setMarriedStatus("Married");
		user.setQualification("Under-Graduate");
		user.setOccupation("business");
		user.setFamilyStatus("moderate");
		user.setTotalFamilyMembers(5); 
		user.setTotalBrothers(2);
		user.setTotalSisters(1);
		user.setPhoneNumber1("1234567890");
		user.setFormFilledBy("Self");

		userRepo.findByEmail(adminEmail).ifPresentOrElse(user1 -> {
		}, () -> {
			userRepo.save(user);
			System.out.println("<==========: Admin has Created Succesfully :==========>");
		});
	}
}
