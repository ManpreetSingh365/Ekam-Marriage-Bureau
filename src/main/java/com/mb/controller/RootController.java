package com.mb.controller;

import org.slf4j.Logger;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.mb.entities.User;
import com.mb.helpers.Helper;
import com.mb.services.UserService;

// As by Declared RootController Class as @ControllerAdvice, So it now, Inside this Class Methods are Applicable for Every Request
@ControllerAdvice
public class RootController {

//    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RootController.class);

	@Autowired
	private UserService userService;

	@Value("${admin.email}")
	private String adminEmail;

	@ModelAttribute
	public void addLoggedInUserInformation(Model model, Authentication authentication) {
		if (authentication == null) {
			return;
		}
		System.out.println("Adding logged in user information to the model");

		String username = Helper.getEmailOfLoggedInUser(authentication);
//        logger.info("User logged in: {}", username);

		// Getting/Fetching User-Data from Database:
		User user = userService.getUserByEmail(username);
		System.out.println(user);
		System.out.println(user.getName());
		System.out.println(user.getEmail());

		if (logger.isDebugEnabled()) {
			logger.debug("User logged in: {}", username);
		}
		model.addAttribute("loggedInUser", user);
		model.addAttribute("adminEmail", adminEmail);
	}

	@ModelAttribute
	public void addGlobalAttributes(Model model, HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		if (!requestUri.equals("/index")) {
			model.addAttribute("includeCss", true);
		}
	}
}