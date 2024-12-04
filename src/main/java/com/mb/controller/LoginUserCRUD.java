package com.mb.controller;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mb.domain.USER_ROLE;
import com.mb.dto.UserProfileDto;
import com.mb.entities.PaymentResponse;
import com.mb.entities.User;
import com.mb.forms.UserForm;
import com.mb.forms.UserFormDetails;
import com.mb.forms.UserFormDetailsAdmin;
import com.mb.helpers.Helper;
import com.mb.helpers.Message;
import com.mb.helpers.MessageType;
import com.mb.helpers.UserDefaultValues;
import com.mb.services.ImageService;
import com.mb.services.PaymentService;
import com.mb.services.TokenService;
import com.mb.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class LoginUserCRUD {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(PageController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserDefaultValues userDefaultValues;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private TokenService tokenService;

	@Value("${server.baseUrl}")
	private String BASE_URL;

	// Showing Particular User Details.....
	@RequestMapping("/user/{userId}")
	public String showUserDetail(@PathVariable("userId") Long userId, Model model) {

		System.out.println("userId of User: " + userId);

		Optional<User> userOptional = this.userService.getUserById(userId);
		User userData = userOptional.get();

//		String username = Helper.getEmailOfLoggedInUser(authentication);
//		User userData = userService.getUserByEmail(username);

		System.out.println("userData.getPaymentResponse()----------------->");
		System.out.println(userData);
		if (userData.getPaymentResponse() != null) {
			System.out.println(userData.getPaymentResponse());
			System.out.println(userData.getPaymentResponse().toString());
			System.out.println(userData.getPaymentResponse().getRazorpayOrderId());
			System.out.println(userData.getPaymentResponse().getRazorpayPaymentId());
			System.out.println(userData.getPaymentResponse().getRazorpaySignature());
		}

		if (paymentService.getUserById(userData.getUserId()).isEmpty()) {
			System.out.println("isEmpty(): " + paymentService.getUserById(userData.getUserId()).isEmpty());
			model.addAttribute("paymentResponse", "empty");
		}
		if (paymentService.getUserById(userData.getUserId()).isPresent()) {
			Optional<PaymentResponse> paymentResponse = paymentService.getUserById(userData.getUserId());
			System.out.println("isPresent(): " + paymentService.getUserById(userData.getUserId()).isPresent());
			PaymentResponse response = paymentResponse.get();
			System.out.println("response: " + response);
			model.addAttribute("paymentResponse", response);
		}

		System.out.println("\n\n\nThis is userId from user.... Process UserProfile  Handler");

		model.addAttribute("user", userData);
		model.addAttribute("userImages", userData.getImagesList());
		model.addAttribute("isLoginProfile", false);

		int size = this.userService.getAllUsers().size();
		model.addAttribute("totalUsers", size);

		return "User/userprofile";
	}

	// Showing Particular User Details.....
	@RequestMapping("/user/logginprofile")
	public String showLoginUserDetail(Model model, Authentication authentication, HttpSession session) {

		String username = Helper.getEmailOfLoggedInUser(authentication);
		User userData = userService.getUserByEmail(username);

		System.out.println("\n\n\nThis is LoginUser from logginprofile.... Process showLoginUserDetail  Handler");

		System.out.println("userData.getPaymentResponse()----------------->");
		System.out.println(userData);
		if (userData.getPaymentResponse() != null) {
			System.out.println(userData.getPaymentResponse());
			System.out.println(userData.getPaymentResponse().toString());
			System.out.println(userData.getPaymentResponse().getRazorpayOrderId());
			System.out.println(userData.getPaymentResponse().getRazorpayPaymentId());
			System.out.println(userData.getPaymentResponse().getRazorpaySignature());
		}

		if (paymentService.getUserById(userData.getUserId()).isEmpty()) {
			System.out.println("isEmpty(): " + paymentService.getUserById(userData.getUserId()).isEmpty());
			model.addAttribute("paymentResponse", "empty");
		}
		if (paymentService.getUserById(userData.getUserId()).isPresent()) {
			Optional<PaymentResponse> paymentResponse = paymentService.getUserById(userData.getUserId());
			System.out.println("isPresent(): " + paymentService.getUserById(userData.getUserId()).isPresent());
			PaymentResponse response = paymentResponse.get();
			System.out.println("response: " + response);
			model.addAttribute("paymentResponse", response);
		}

		// if (paymentResponse.isEmpty())
//			System.out.println("paymentResponse isEmpty(): " + paymentResponse);
//
//		if (paymentResponse.isPresent())
//			System.out.println("paymentResponse isPresent(): " + paymentResponse);
////		System.out.println("paymentResponse.get(): " + paymentResponse.get());

//		if (paymentResponse.isEmpty()) {
//			model.addAttribute("paymentResponse", "empty"); // or some default value
//		} else {
//			PaymentResponse response = paymentResponse.get();
//			System.out.println("response: " + response);
//			model.addAttribute("paymentResponse", response);
//		}

//		List<PaymentResponse> allPaymentResponses = paymentService.getAllPaymentResponses();
//		for (PaymentResponse pr : allPaymentResponses) {
//			System.out.println("pr: " + pr);
//		}

		model.addAttribute("user", userData);
		model.addAttribute("userImages", userData.getImagesList());
		model.addAttribute("isLoginProfile", true);
		return "User/userprofile";
	}

//  Open Update_Contact Handler----->
	@GetMapping("/view/userDetailsUpdateForm")
	public String updateUserFormView(Model model, Authentication authentication) {

		System.out.println("updateUserFormView Handler..........");
		String username = Helper.getEmailOfLoggedInUser(authentication);
		User userData = userService.getUserByEmail(username);

		UserFormDetails userFormDetails = new UserFormDetails();

		userFormDetails.setYourName(userData.getName());
		userFormDetails.setGender(userData.getGender());
		userFormDetails.setReligion(userData.getReligion());
//		userFormDetails.setCaste(userData.getCaste());
		userFormDetails.setSubcaste(userData.getSubcaste());

		userFormDetails.setDateOfBirth(userData.getDateOfBirth());
		userFormDetails.setAge(userData.getAge());
		userFormDetails.setBrithTime(userData.getBrithTime());
		userFormDetails.setHeight(userData.getHeight());
		userFormDetails.setMarriedStatus(userData.getMarriedStatus());
		userFormDetails.setPlace(userData.getPlace());
		userFormDetails.setNriPlace(userData.getNriPlace());
		userFormDetails.setQualification(userData.getQualification());
		userFormDetails.setQualificationField(userData.getQualificationField());
		userFormDetails.setOccupation(userData.getOccupation());
		userFormDetails.setYourJobTitle(userData.getYourJobTitle());
		userFormDetails.setYourJobSalary(userData.getYourJobSalary());

		userFormDetails.setFamilyStatus(userData.getFamilyStatus());
		userFormDetails.setTotalFamilyMembers(userData.getTotalFamilyMembers());
		userFormDetails.setTotalBrothers(userData.getTotalBrothers());
		userFormDetails.setTotalSisters(userData.getTotalSisters());

		userFormDetails.setFatherName(userData.getFatherName());
		userFormDetails.setFatherOccupation(userData.getFatherOccupation());
		userFormDetails.setFatherJobTitle(userData.getFatherJobTitle());
		userFormDetails.setFatherJobSalary(userData.getFatherJobSalary());

		userFormDetails.setMotherName(userData.getMotherName());
		userFormDetails.setMotherOccupation(userData.getMotherOccupation());
		userFormDetails.setMotherJobTitle(userData.getMotherJobTitle());
		userFormDetails.setMotherJobSalary(userData.getMotherJobSalary());

		userFormDetails.setAnyDemand(userData.getAnyDemand());
		userFormDetails.setAnyRemarks(userData.getAnyRemarks());
		userFormDetails.setAddress(userData.getAddress());

		userFormDetails.setPhoneNumber1(userData.getPhoneNumber1());
		userFormDetails.setPhoneNumber1(userData.getPhoneNumber1());
		userFormDetails.setPhoneNumber2(userData.getPhoneNumber2());

		userFormDetails.setFormFilledBy(userData.getFormFilledBy());

		// Fetch distinct Religions, castes categories from the database
		List<String> religions = userService.getAllDistinctReligions();
		List<String> castes = userService.getAllDistinctCastes(userData.getReligion());

		model.addAttribute("religions", religions);
		model.addAttribute("castes", castes);

		for (String val : religions) {
			System.out.println(val);
		}
		for (String val : castes) {
			System.out.println(val);
		}

		model.addAttribute("userFormDetails", userFormDetails);
		model.addAttribute("userImages", userData.getImagesList());

		return "User/update_user_view";
	}

//  Processing for Update_Contact Handler----->
	@PostMapping("/update/userDetailsUpdateForm")
	public String processUpdateUserFormView(@Valid @ModelAttribute("userFormDetails") UserFormDetails userFormDetails,
			BindingResult bindingResult, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
			@RequestParam("userImages") List<MultipartFile> userImages, Model model, Authentication authentication,
			HttpSession session) throws Exception {

		System.out.println("processUpdateUserFormView Handler..........");
		String username = Helper.getEmailOfLoggedInUser(authentication);
		User userData = userService.getUserByEmail(username);

		if (!agreement) {
			throw new Exception("You must agree to the terms and conditions.");
		}

		// update the contact
		if (bindingResult.hasErrors()) {
			return "user/update_user_view";
		}

		userDefaultValues.setDefaultValues(userFormDetails);

		userData.setName(userFormDetails.getYourName());
		userData.setGender(userFormDetails.getGender());
		userData.setReligion(userFormDetails.getReligion());
		userData.setCaste(userFormDetails.getCaste());
		userData.setSubcaste(userFormDetails.getSubcaste());

//		String dateOfBirth = userFormDetails.getDateOfBirth();
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//		LocalDate dob = LocalDate.parse(dateOfBirth, formatter);
//		int age = Period.between(dob, LocalDate.now()).getYears();

		// Getting Age from DOB...
		String dateOfBirth = userFormDetails.getDateOfBirth(); // This is in "MM/dd/yyyy"
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate dob = LocalDate.parse(dateOfBirth, inputFormatter); // Parse input date
		String formattedDateOfBirth = dob.format(outputFormatter); // Format to "dd/MM/yyyy"

		int age = Period.between(dob, LocalDate.now()).getYears(); // Calculate age

		userData.setDateOfBirth(dateOfBirth);
		userData.setAge(age);
		userData.setBrithTime(userFormDetails.getBrithTime());
		userData.setHeight(userFormDetails.getHeight());
		userData.setMarriedStatus(userFormDetails.getMarriedStatus());
		userData.setPlace(userFormDetails.getPlace());
		userData.setNriPlace(userFormDetails.getNriPlace());
		userData.setQualification(userFormDetails.getQualification());
		userData.setQualificationField(userFormDetails.getQualificationField());
		userData.setOccupation(userFormDetails.getOccupation());
		userData.setYourJobTitle(userFormDetails.getYourJobTitle());
		userData.setYourJobSalary(userFormDetails.getYourJobSalary());

		userData.setFamilyStatus(userFormDetails.getFamilyStatus());
		userData.setTotalFamilyMembers(userFormDetails.getTotalFamilyMembers());
		userData.setTotalBrothers(userFormDetails.getTotalBrothers());
		userData.setTotalSisters(userFormDetails.getTotalSisters());

		userData.setFatherName(userFormDetails.getFatherName());
		userData.setFatherOccupation(userFormDetails.getFatherOccupation());
		userData.setFatherJobTitle(userFormDetails.getFatherJobTitle());
		userData.setFatherJobSalary(userFormDetails.getFatherJobSalary());

		userData.setMotherName(userFormDetails.getMotherName());
		userData.setMotherOccupation(userFormDetails.getMotherOccupation());
		userData.setMotherJobTitle(userFormDetails.getMotherJobTitle());
		userData.setMotherJobSalary(userFormDetails.getMotherJobSalary());

		userData.setAnyDemand(userFormDetails.getAnyDemand());
		userData.setAnyRemarks(userFormDetails.getAnyRemarks());
		userData.setAddress(userFormDetails.getAddress());

		userData.setPhoneNumber1(userFormDetails.getPhoneNumber1());
		userData.setPhoneNumber1(userFormDetails.getPhoneNumber1());
		userData.setPhoneNumber2(userFormDetails.getPhoneNumber2());

		userData.setFormFilledBy(userFormDetails.getFormFilledBy());

		if (userImages != null && !userImages.isEmpty()) {
			List<String> imageUrls = imageService.uploadImages(userImages, UUID.randomUUID().toString());
			List<String> publicIds = new ArrayList<>();

			for (int i = 0; i < imageUrls.size(); i++) {
				publicIds.add(UUID.randomUUID().toString());
			}

//			userData.setPicture(imageUrls.get(0)); // set the first image as the profile picture
//			userData.setImages(imageUrls); // store the list of image URLs
			userData.setImagesList(imageUrls); // store the list of image URLs

			System.out.println("URL-------------------");
			System.out.println(imageUrls);
			System.out.println("Public IDs-------------------");
			System.out.println(publicIds);
		}

		var updateUser = userService.updateUser(userData);
		logger.info("Updated User {}", updateUser);

		model.addAttribute("message", Message.builder().content("User Updated !!").type(MessageType.green).build());

		// Adding Message that Register Successfully :)
		Message message = Message.builder().content("Your Data is Updated Successful :)").type(MessageType.green)
				.build();
		session.setAttribute("message", message);

		return "redirect:/user/logginprofile";
	}

//  Open Update_Contact Handler by Admin ----->
	@GetMapping("/view/userDetailsUpdateForm/{userId}")
	public String updateUserFormViewAdmin(@PathVariable("userId") Long userId, Model model) {

		System.out.println("updateUserFormView Handler..........");

		Optional<User> userOptional = this.userService.getUserById(userId);
		User userData = userOptional.get();

		UserFormDetailsAdmin userFormDetailsAdmin = new UserFormDetailsAdmin();

		model.addAttribute("userId", userId);
		model.addAttribute("userFormDetails", userFormDetailsAdmin);
		model.addAttribute("userImages", userData.getImagesList());

		return "User/update_user_view_by_admin";
	}

//  Processing for Update_Contact Handler by Admin ----->
	@PostMapping("/update/userDetailsUpdateForm/{userId}")
	public String processUpdateUserFormViewAdmin(
			@Valid @ModelAttribute("userFormDetails") UserFormDetailsAdmin userFormDetailsAdmin,
			BindingResult bindingResult, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
			@RequestParam("userImages") List<MultipartFile> userImages, @PathVariable("userId") Long userId,
			Model model, HttpSession session) throws Exception {

		System.out.println("processUpdateUserFormView Handler..........");

		Optional<User> userOptional = this.userService.getUserById(userId);
		User userData = userOptional.get();

		// If there are validation errors, return to the same form view
		if (bindingResult.hasErrors()) {
			return "user/update_user_view";
		}

		// Process and upload images if provided
		if (userImages != null && !userImages.isEmpty()) {
			List<String> imageUrls = imageService.uploadImages(userImages, UUID.randomUUID().toString());

			System.out.println("URL-------------------");
			System.out.println(imageUrls);
			// Set image data on the user object
			userData.setImagesList(imageUrls); // store the list of image URLs
		}

		// Update the user in the database
		var updateUser = userService.updateUser(userData);
		logger.info("Updated User {}", updateUser);

		// Add user ID and success message to the model
		model.addAttribute("userId", userId);
		model.addAttribute("message", Message.builder().content("User Updated !!").type(MessageType.green).build());

		// Set a success message in the session
		Message message = Message.builder().content("Your Data is Updated Successful :)").type(MessageType.green)
				.build();
		session.setAttribute("message", message);

		// Redirect to the user details page after update
		// return "redirect:/user/" + userId;
		return "redirect:/user/userlist";
	}

	@GetMapping("/do-deleteclient/{userId}")
	public String deleteUserByClient(@PathVariable("userId") Long userId, Model model, HttpSession session) {
		Optional<User> userOptional = this.userService.getUserById(userId);

		if (userOptional.isEmpty()) {
			// Handle case where user is not found
			session.setAttribute("message", new Message("User not found", MessageType.red));
			return "redirect:/user/userlist"; // Redirect to a safe page
		}

		User userData = userOptional.get();
		userService.deleteUser(userData); // Deleting user from DB

		// After deleting, invalidate the session to ensure the user is logged out
		session.invalidate(); // Invalidate the session to remove any references to the user

		// Optional: You can redirect to login page or a safe page after account
		// deletion
		session.setAttribute("message", new Message("Your account has been deleted successfully.", MessageType.green));
		return "redirect:/login"; // Redirect to login page, or any page you want after deletion
	}

	// Delete User Admin Handler----->
	@GetMapping("/do-deleteadmin/{userId}")
	public String deleteUserByAdmin(@PathVariable("userId") Long userId, Model model, HttpSession session) {

		System.out.println("deleteUser Handler..........");
		Optional<User> userOptional = this.userService.getUserById(userId);
		User userData = userOptional.get();

		// delete the user
		userService.deleteUser(userData);

		// Adding Message that User Deleted Successfully :)
		Message message = Message.builder().content("User Deleted Successful by Admin :)").type(MessageType.green)
				.build();
		session.setAttribute("message", message);

		return "redirect:/user/userlist";
	}

	@GetMapping("/do-deleteimgadmin/{userId}")
	public String deleteAllUserImagesByAdmin(@PathVariable("userId") Long userId, Model model, HttpSession session) {
		System.out.println("deleteAllUserImagesByAdmin Handler..........");

		// Fetch the user by ID
		Optional<User> userOptional = userService.getUserById(userId);
		User userData = userOptional.get();

		// Create a modifiable copy of the images list
		List<String> imagesList = new ArrayList<>(userData.getImagesList());
//		System.out.println("Images before deletion: " + imagesList.toString());

		// Clear the list
		imagesList.clear();

//		System.out.println("Images after deletion: " + imagesList.toString());
		imagesList.add("https://res.cloudinary.com/dcrlfty5k/image/upload/v1729153915/yjllp8ag6uab4gdq7hog.png");
//		System.out.println("Added Images after deletion: " + imagesList.toString());

		// Set the modified list back to the user object
		userData.setImagesList(imagesList);

		userService.updateUser(userData);

		return "redirect:/user/userlist";
	}

	// Delete User Handler----->
	@GetMapping("/do-togglesubscriptionisactive/{userId}")
	public String toggleSubscriptionIsActive(@PathVariable("userId") Long userId, Model model) {

		System.out.println("ToggleSubscriptionIsActive Handler..........");
		Optional<User> userOptional = this.userService.getUserById(userId);
		User userData = userOptional.get();

		System.out.println("Before- userData.isSubscriptionIsActive(): " + userData.isSubscriptionIsActive());
		if (userData.isSubscriptionIsActive()) {
			System.out.println("insdie-true userData.isSubscriptionIsActive(): " + userData.isSubscriptionIsActive());
			userData.setSubscriptionIsActive(false);
		} else {
			System.out.println("insdie-false userData.isSubscriptionIsActive(): " + userData.isSubscriptionIsActive());
			userData.setSubscriptionIsActive(true);
		}
		System.out.println("After- userData.isSubscriptionIsActive(): " + userData.isSubscriptionIsActive());

		// Update subscriptionIsActive the user
		userService.updateUser(userData);

		return "redirect:/user/userlist";
	}

	// Share Profile to anyone-------->
	// Endpoint for generating a shareable link with a token
	@GetMapping("/generateShareLink/{userId}")
	public ResponseEntity<String> generateShareLink(@PathVariable Long userId) {
		// Generate the token for the given userId
		String token = tokenService.generateToken(userId);

		System.out.println("Generated Token: " + token);

		// Construct the URL for sharing
		String shareableLink = BASE_URL + "/shareprofile/" + userId + "/" + token;

		// Return the generated link as a response
		return ResponseEntity.ok(shareableLink);
	}

	@RequestMapping("/shareprofile/{userId}/{token}")
	public String getShareableUserProfile(@PathVariable Long userId, @PathVariable String token, Model model) {
		System.out.println("userId of User: " + userId);

		// Validate the token
		if (!tokenService.isValidToken(userId, token)) {
			return "NotAuthorizedAccess"; // Forbidden if token is invalid
		}

		// Retrieve the user profile by userId
		Optional<User> userOptional = userService.getUserById(userId);

		// Check if user exists
		if (userOptional.isPresent()) {
			User userData = userOptional.get(); // Retrieve user data

			System.out.println("User Data: " + userData);

			// Handle payment response, if available
			if (userData.getPaymentResponse() != null) {
				PaymentResponse paymentResponse = userData.getPaymentResponse();
				System.out.println("Payment Response: " + paymentResponse);
				System.out.println("Razorpay Order ID: " + paymentResponse.getRazorpayOrderId());
				System.out.println("Razorpay Payment ID: " + paymentResponse.getRazorpayPaymentId());
				System.out.println("Razorpay Signature: " + paymentResponse.getRazorpaySignature());
			}

			// Check if payment response exists in the payment service
			Optional<PaymentResponse> paymentResponseOptional = paymentService.getUserById(userData.getUserId());

			if (paymentResponseOptional.isEmpty()) {
				System.out.println("No payment response found for user: " + userData.getUserId());
				model.addAttribute("paymentResponse", "empty");
			} else {
				PaymentResponse paymentResponse = paymentResponseOptional.get();
				System.out.println("Payment Response found: " + paymentResponse);
				model.addAttribute("paymentResponse", paymentResponse);
			}

			// Add user and related data to the model
			model.addAttribute("user", userData);
			model.addAttribute("userImages", userData.getImagesList());
			model.addAttribute("isLoginProfile", false); // Since it's a shareable profile, this is false

			// Add total number of users
			int totalUsers = userService.getAllUsers().size();
			model.addAttribute("totalUsers", totalUsers);

			return "User/userprofile"; // Return the user profile page

		} else {
			// If no user is found for the provided userId
			return "matchedusernotfound"; // User not found page
		}
	}

}
