package com.mb.utils;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mb.services.UserService;

@Component
public class UniqueEmailGenerator {

	@Autowired
	private UserService userService;

	public boolean emailExists(String email) {
		return userService.isUserExistByEmail(email);
	}

	// Method to generate a random number and check for uniqueness
	public String generateUniqueEmail() {
		Random random = new Random();
		String emailString;
		String email;

		// Loop until a unique email is found
		do {
			int randomNumber = random.nextInt(100000, 999999);
			emailString = "emp" + randomNumber + "@gmail.com";
			email = emailString; // This is the email that will be checked for uniqueness
		} while (emailExists(email)); // Check if the email already exists in the database

		return email; // Return the unique email
	}
	
	
	
	
}
