package com.mb.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mb.forms.UserForm;
import com.mb.forms.UserFormDetails;
import com.mb.helpers.AppConstants;
import com.mb.helpers.Helper;
import com.mb.helpers.Message;
import com.mb.helpers.MessageType;
import com.mb.services.TestimonialsService;
import com.mb.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

import com.mb.domain.USER_ROLE;
import com.mb.entities.Testimonials;
import com.mb.entities.User;
import org.springframework.web.servlet.view.RedirectView; // Import this class

@Controller
public class PageController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = { "/", "/index" })
	public String index(Model model) {

		UserFormDetails userFormDetails = new UserFormDetails();
		model.addAttribute("userFormDetails", userFormDetails);

		// Fetch distinct relisions, castes categories from the database
		List<String> religions = userService.getAllDistinctReligions();
		List<String> castes = userService.getAllDistinctCastes(userFormDetails.getReligion());

		model.addAttribute("religions", religions);
		model.addAttribute("castes", castes);
		System.out.println(religions);

		return "index";
	}

	@RequestMapping("/home")
	public String home() {
		return "index";
	}

	@RequestMapping("/base")
	public String base(Authentication authentication, Model model) {
		String username = Helper.getEmailOfLoggedInUser(authentication);
		User userData = userService.getUserByEmail(username);

		model.addAttribute("userData", userData);

		return "base";
	}

	@RequestMapping("/gallery")
	public RedirectView Gallery() {
		return new RedirectView("/index#gallery");
	}
	
	@RequestMapping("/about")
	public RedirectView About() {
		return new RedirectView("/index#about");
	}

	@RequestMapping("/contact-us")
	public RedirectView ContactUs() {
		return new RedirectView("/index#rsvp");
	}


	@RequestMapping("/paidSuccessfully")
	public String paidSuccessfully() {
		return "paidSuccessfully";
	}

	@RequestMapping("/termcondition")
	public String termcondition() {
		return "termcondition";
	}

	@RequestMapping("/policy")
	public String policy() {
		return "policy";
	}

	@RequestMapping("/paymentplans")
	public String paymentplans(Model model, Authentication authentication) {
		Optional<Authentication> authOptional = Optional.ofNullable(authentication);

		model.addAttribute("authOptional", authOptional.isPresent());
		model.addAttribute("GOLD_PLAN_PRICE", AppConstants.GOLD_PLAN_PRICE);
		model.addAttribute("DIAMOND_PLAN_PRICE", AppConstants.DIAMOND_PLAN_PRICE);
		model.addAttribute("PLATINUM_PLAN_PRICE", AppConstants.PLATINUM_PLAN_PRICE);

		model.addAttribute("rzp_key_id", "rzp_live_xko5HzmXhUOjwN");
		model.addAttribute("rzp_key_secret", "n2xolVPLJ88zOuCyrmbAnt73");
//	    List<Map<String, Object>> plans = new ArrayList<>();
//	    
//	    // Adding Gold Plan
//	    plans.add(new HashMap<>() {{
//	        put("name", "GOLD");
//	        put("price", AppConstants.GOLD_PLAN_PRICE);
//	        put("duration", "1 month");
//	        put("validityPeriod", 1);
//	        put("validityType", "months");
//	        put("features", Arrays.asList("Connect directly with Matches", "View detailed Profile information", "View Contact number"));
//	        put("type", "gold");
//	    }});
//
//	    // Adding Diamond Plan
//	    plans.add(new HashMap<>() {{
//	        put("name", "DIAMOND");
//	        put("price", AppConstants.DIAMOND_PLAN_PRICE);
//	        put("duration", "3 months");
//	        put("validityPeriod", 3);
//	        put("validityType", "months");
//	        put("features", Arrays.asList("Connect directly with Matches", "View detailed Profile information", "Get Highlighted to your Matches", "View Contact number"));
//	        put("type", "diamond");
//	    }});
//
//	    // Adding Platinum Plan
//	    plans.add(new HashMap<>() {{
//	        put("name", "PLATINUM");
//	        put("price", AppConstants.PLATINUM_PLAN_PRICE);
//	        put("duration", "Lifetime");
//	        put("validityPeriod", 1);
//	        put("validityType", "years");
//	        put("features", Arrays.asList("Dedicated Relationship Advisor", "Introduction and Meeting", "Handpicked Matches", "Be featured under Spotlight", "Standout with Bold Listing", "All Other Premium Benefits"));
//	        put("type", "platinum");
//	    }});
//
//	    model.addAttribute("plans", plans);

		return "paymentplans";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@RequestMapping("/notauthorizedaccess")
	public String NotAuthorizedAccess() {
		return "NotAuthorizedAccess";
	}

	@RequestMapping("/logintopay")
	public String logintopay(HttpSession session) {
		// Adding Message that Register Successfully :)
		Message message = Message.builder().content("Login (Register if Needed) to Access Payment-Plans")
				.type(MessageType.green).build();
		session.setAttribute("message", message);

		return "login";
	}

//  Open for Register Page Handler----->
	@RequestMapping("/register")
	public String registeration(Model model) {
		System.out.println("Opening Registeration Handler...");

		UserForm userForm = new UserForm();
		model.addAttribute("userForm", userForm);

		return "register";
	}

}