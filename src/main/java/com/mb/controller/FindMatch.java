package com.mb.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mb.domain.USER_ROLE;
import com.mb.entities.User;
import com.mb.forms.UserFormDetails;
import com.mb.helpers.AppConstants;
import com.mb.services.UserService;
import com.mb.helpers.Helper;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

@Controller
public class FindMatch {

	@Autowired
	private UserService userService;

	@Value("${admin.email}")
	private String adminEmail;

	@RequestMapping(value = "/getCastes", method = RequestMethod.GET)
	@ResponseBody
	public List<String> getCastesByReligion(@RequestParam String religion) {
		System.out.println("Fetching castes for religion: " + religion);
		return userService.getAllDistinctCastes(religion);
	}

//  Open for FindMatch Page Handler----->
	@RequestMapping("/findmatch")
	public String findmatch(Model model) {
		System.out.println("Opening findMatch Handler...");

		UserFormDetails userFormDetails = new UserFormDetails();
		model.addAttribute("userFormDetails", userFormDetails);

		// Fetch distinct relisions, castes categories from the database
		List<String> religions = userService.getAllDistinctReligions();
		List<String> castes = userService.getAllDistinctCastes(userFormDetails.getReligion());
		List<String> qualification = userService.getAllDistinctQualification();
		List<String> occupation = userService.getAllDistinctOccupation();

		model.addAttribute("religions", religions);
		model.addAttribute("castes", castes);
		model.addAttribute("qualification", qualification);
		model.addAttribute("occupation", occupation);

		return "findmatch";
	}

//  Processing for FindMatching Handler----->
	@RequestMapping("/do-homefindmatch")
	public String processHomeFindmatchUser(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "userId") String sortBy,
			@RequestParam(value = "direction", defaultValue = "asc") String direction,
			@RequestParam(value = "gender", required = true) String gender,
			@RequestParam(value = "religion", required = true) String religion,
			@RequestParam(value = "caste", required = true) String caste,
			@RequestParam(value = "minAge", defaultValue = "18") Integer minAge,
			@RequestParam(value = "maxAge", defaultValue = "100") Integer maxAge,
			@RequestParam(value = "place") String place, Model model, Authentication authentication) throws Exception {

		// Fetch Form-Data from UserForm to bind with Model_Object by @ModelAttribute
		System.out.println("\nProcessing Process user/do-findmatch Handler...");

		// Set default values if parameters are missing or invalid
//		minAge = (minAge == null || minAge <= 0) ? 18 : minAge;
//		maxAge = (maxAge == null || maxAge <= 0) ? 75 : maxAge;

		Float minHeight = 2.2f, maxHeight = 8.8f;
//		minHeight = (minHeight == null || minHeight <= 0.0f) ? 2.2f : minHeight;
//		maxHeight = (maxHeight == null || maxHeight <= 0.0f) ? 8.8f : maxHeight;

//		String marriedStatus = "single";
//		marriedStatus = (marriedStatus == null || marriedStatus.trim().isEmpty()) ? "single" : marriedStatus;
//		place = (place == null || place.trim().isEmpty()) ? "Indian" : place;

		// Set qualification and occupation to null if not provided, allowing the query
		// to include all options
//		String qualification = null, occupation = null;
//		qualification = (qualification == null || qualification.trim().isEmpty()) ? null : qualification;
//		occupation = (occupation == null || occupation.trim().isEmpty()) ? null : occupation;

		System.out.println("minAge: " + minAge);
		System.out.println("maxAge: " + maxAge);
		System.out.println("minHeight: " + minHeight);
		System.out.println("maxHeight: " + maxHeight);
//		System.out.println("marriedStatus: " + marriedStatus);
		System.out.println("place: " + place);
//		System.out.println("qualification: " + qualification);
//		System.out.println("occupation: " + occupation);

		UserFormDetails userFormDetails = new UserFormDetails();
		userFormDetails.setGender(gender);
		userFormDetails.setReligion(religion);
		userFormDetails.setCaste(caste);
		userFormDetails.setMinAge(minAge);
		userFormDetails.setMaxAge(maxAge);
		userFormDetails.setMinHeight(minHeight);
		userFormDetails.setMaxHeight(maxHeight);
//		userFormDetails.setMarriedStatus(marriedStatus);
		userFormDetails.setPlace(place);
//		userFormDetails.setQualification(qualification);
//		userFormDetails.setOccupation(occupation);

		// Save UserForm Data to Database [ UserForm -> Created_User -> Database ]
		// userId | gender | religion | caste | age | height | marriedStatus | place |
		// occupation
		User user = new User();
		user.setGender(userFormDetails.getGender());
		user.setReligion(userFormDetails.getReligion());
		user.setCaste(userFormDetails.getCaste());
		user.setMinAge(userFormDetails.getMinAge());
		user.setMaxAge(userFormDetails.getMaxAge());
		user.setMinHeight(userFormDetails.getMinHeight());
		user.setMaxHeight(userFormDetails.getMaxHeight());
//		user.setMarriedStatus(userFormDetails.getMarriedStatus());
		user.setPlace(userFormDetails.getPlace());
//		user.setQualification(userFormDetails.getQualification());
//		user.setOccupation(userFormDetails.getOccupation());

		Page<User> pageContent = userService.findMatchUserDetailsByFilter(user, page, size, sortBy, direction);

		Optional<Authentication> authOptional = Optional.ofNullable(authentication);
		System.out.println("authOptional: " + authOptional);
		System.out.println("authOptional toString: " + authOptional.toString());
		System.out.println("authOptional toString: " + authOptional.isPresent());
		System.out.println("authOptional toString: " + authOptional.isEmpty());
		if (authOptional.isPresent()) {
			String username = Helper.getEmailOfLoggedInUser(authOptional.get());
			User userData = userService.getUserByEmail(username);
			model.addAttribute("isSubscriptionIsActive", userData.isSubscriptionIsActive());
		}

		model.addAttribute("authOptional", authOptional.isPresent());

		model.addAttribute("foundTotalMatches", pageContent.getTotalElements());
		model.addAttribute("pageContent", pageContent);
		model.addAttribute("pageSize", size); // Add the current page size to the model
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("direction", direction);
		model.addAttribute("userFormDetails", userFormDetails);

		int totalPages = pageContent.getTotalPages();
		int currentPage = pageContent.getNumber();
		int pageWindow = 5; // How many pages to display in the pagination window

		int startPage = Math.max(1, currentPage - (pageWindow / 2));
		int endPage = Math.min(totalPages, currentPage + (pageWindow / 2));

		// Ensure there are always 5 pages displayed, adjusting start or end if
		// necessary
		if (endPage - startPage + 1 < pageWindow) {
			if (startPage == 1) {
				endPage = Math.min(pageWindow, totalPages);
			} else if (endPage == totalPages) {
				startPage = Math.max(1, totalPages - pageWindow + 1);
			}
		}

		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", currentPage);

		if (pageContent.isEmpty()) {
			return "/matchedusernotfound";
		}

		return "matcheduserlist";
	}

//  Processing for FindMatching Handler----->
	@RequestMapping("/do-findmatch")
	public String processFindmatchUser(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "userId") String sortBy,
			@RequestParam(value = "direction", defaultValue = "asc") String direction,
			@RequestParam(value = "gender", required = true) String gender,
			@RequestParam(value = "religion", required = true) String religion,
			@RequestParam(value = "caste", required = true) String caste,
			@RequestParam(value = "minAge") Integer minAge, @RequestParam(value = "maxAge") Integer maxAge,
			@RequestParam(value = "minHeight") Float minHeight, @RequestParam(value = "maxHeight") Float maxHeight,
			@RequestParam(value = "marriedStatus") String marriedStatus, @RequestParam(value = "place") String place,
			@RequestParam(value = "qualification", required = false) String qualification,
			@RequestParam(value = "occupation", required = false) String occupation, Model model,
			Authentication authentication) throws Exception {

		// Fetch Form-Data from UserForm to bind with Model_Object by @ModelAttribute
		System.out.println("\nProcessing Process user/do-findmatch Handler...");
		System.out.println("minAge: " + minAge);
		System.out.println("maxAge: " + maxAge);
		System.out.println("minHeight: " + minHeight);
		System.out.println("maxHeight: " + maxHeight);
		System.out.println("marriedStatus: " + marriedStatus);
		System.out.println("place: " + place);
		System.out.println("qualification: " + qualification);
		System.out.println("occupation: " + occupation);

		// Set default values if parameters are missing or invalid
		minAge = (minAge == null || minAge <= 0) ? 18 : minAge;
		maxAge = (maxAge == null || maxAge <= 0) ? 75 : maxAge;
		minHeight = (minHeight == null || minHeight <= 0.0f) ? 2.2f : minHeight;
		maxHeight = (maxHeight == null || maxHeight <= 0.0f) ? 8.8f : maxHeight;
//		marriedStatus = (marriedStatus == null || marriedStatus.trim().isEmpty()) ? "single" : marriedStatus;
//		place = (place == null || place.trim().isEmpty()) ? "Indian" : place;

		// Set qualification and occupation to null if not provided, allowing the query
		// to include all options
		qualification = (qualification == null || qualification.trim().isEmpty()) ? null : qualification;
		occupation = (occupation == null || occupation.trim().isEmpty()) ? null : occupation;

		System.out.println("minAge: " + minAge);
		System.out.println("maxAge: " + maxAge);
		System.out.println("minHeight: " + minHeight);
		System.out.println("maxHeight: " + maxHeight);
		System.out.println("marriedStatus: " + marriedStatus);
		System.out.println("place: " + place);
		System.out.println("qualification: " + qualification);
		System.out.println("occupation: " + occupation);

		UserFormDetails userFormDetails = new UserFormDetails();
		userFormDetails.setGender(gender);
		userFormDetails.setReligion(religion);
		userFormDetails.setCaste(caste);
		userFormDetails.setMinAge(minAge);
		userFormDetails.setMaxAge(maxAge);
		userFormDetails.setMinHeight(minHeight);
		userFormDetails.setMaxHeight(maxHeight);
		userFormDetails.setMarriedStatus(marriedStatus);
		userFormDetails.setPlace(place);
		userFormDetails.setQualification(qualification);
		userFormDetails.setOccupation(occupation);

		// Save UserForm Data to Database [ UserForm -> Created_User -> Database ]
		// userId | gender | religion | caste | age | height | marriedStatus | place |
		// occupation
		User user = new User();
		user.setGender(userFormDetails.getGender());
		user.setReligion(userFormDetails.getReligion());
		user.setCaste(userFormDetails.getCaste());
		user.setMinAge(userFormDetails.getMinAge());
		user.setMaxAge(userFormDetails.getMaxAge());
		user.setMinHeight(userFormDetails.getMinHeight());
		user.setMaxHeight(userFormDetails.getMaxHeight());
		user.setMarriedStatus(userFormDetails.getMarriedStatus());
		user.setPlace(userFormDetails.getPlace());
		user.setQualification(userFormDetails.getQualification());
		user.setOccupation(userFormDetails.getOccupation());

		Page<User> pageContent = userService.findMatchUserDetailsByFilter(user, page, size, sortBy, direction);

		Optional<Authentication> authOptional = Optional.ofNullable(authentication);
		System.out.println("authOptional: " + authOptional);
		System.out.println("authOptional toString: " + authOptional.toString());
		System.out.println("authOptional toString: " + authOptional.isPresent());
		System.out.println("authOptional toString: " + authOptional.isEmpty());
		if (authOptional.isPresent()) {
			String username = Helper.getEmailOfLoggedInUser(authOptional.get());
			User userData = userService.getUserByEmail(username);
			model.addAttribute("isSubscriptionIsActive", userData.isSubscriptionIsActive());
			System.out.println("userData.isSubscriptionIsActive(): " + userData.isSubscriptionIsActive());
		}

		model.addAttribute("authOptional", authOptional.isPresent());

		model.addAttribute("foundTotalMatches", pageContent.getTotalElements());
		model.addAttribute("pageContent", pageContent);
		model.addAttribute("pageSize", size); // Add the current page size to the model
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("direction", direction);
		model.addAttribute("userFormDetails", userFormDetails);

		int totalPages = pageContent.getTotalPages();
		int currentPage = pageContent.getNumber();
		int pageWindow = 5; // How many pages to display in the pagination window

		int startPage = Math.max(1, currentPage - (pageWindow / 2));
		int endPage = Math.min(totalPages, currentPage + (pageWindow / 2));

		// Ensure there are always 5 pages displayed, adjusting start or end if
		// necessary
		if (endPage - startPage + 1 < pageWindow) {
			if (startPage == 1) {
				endPage = Math.min(pageWindow, totalPages);
			} else if (endPage == totalPages) {
				startPage = Math.max(1, totalPages - pageWindow + 1);
			}
		}

		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", currentPage);

		if (pageContent.isEmpty()) {
			return "/matchedusernotfound";
		}

		return "matcheduserlist";
	}

	@RequestMapping("/user/userlist")
	public String userList(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "1000") int size, // Allow dynamic size
			@RequestParam(value = "sortBy", defaultValue = "userId") String sortBy,
			@RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
			Authentication authentication) {

		// Illegal Access Handler
		String username = Helper.getEmailOfLoggedInUser(authentication);
		User userData = userService.getUserByEmail(username);

//		if (!userData.getEmail().equals(adminEmail)) {
//			return "NotAuthorizedAccess";
//		}

		if (!userData.hasAnyRole(USER_ROLE.ROLE_ADMIN, USER_ROLE.ROLE_EMPLOYEE)) {
		    return "NotAuthorizedAccess";
		}

		Page<User> pageContent = userService.getByUser(page, size, sortBy, direction);

		model.addAttribute("pageContent", pageContent);
		model.addAttribute("pageSize", size); // Add the current page size to the model

		int totalPages = pageContent.getTotalPages();
		int currentPage = pageContent.getNumber();
		int pageWindow = 5; // How many pages to display in the pagination window

		int startPage = Math.max(1, currentPage - (pageWindow / 2));
		int endPage = Math.min(totalPages, currentPage + (pageWindow / 2));

		// Ensure there are always 5 pages displayed, adjusting start or end if
		// necessary
		if (endPage - startPage + 1 < pageWindow) {
			if (startPage == 1) {
				endPage = Math.min(pageWindow, totalPages);
			} else if (endPage == totalPages) {
				startPage = Math.max(1, totalPages - pageWindow + 1);
			}
		}

		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentPage", currentPage);

		List<User> users = userService.getAllUsers();
		if (users.isEmpty()) {
			model.addAttribute("users", Collections.emptyList());
		} else {
			model.addAttribute("users", users);
			model.addAttribute("adminEmail", adminEmail);
		}

		return "User/userlist";
	}

}
