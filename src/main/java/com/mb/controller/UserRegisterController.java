package com.mb.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mb.domain.USER_ROLE;
import com.mb.entities.User;
import com.mb.forms.UserForm;
import com.mb.forms.UserFormDetails;
import com.mb.helpers.Message;
import com.mb.helpers.MessageType;
import com.mb.helpers.UserDefaultValues;
import com.mb.services.ImageService;
import com.mb.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;

import com.mb.utils.UniqueEmailGenerator;
import java.util.Random;

@Controller
public class UserRegisterController {

	@Autowired
	private UserService userService;

	@Autowired
	private UniqueEmailGenerator uniqueEmailGenerator;

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserDefaultValues userDefaultValues;

//  Open for Register Page Handler----->
	@RequestMapping("/registerdetails")
	public String registerationDetails(@Valid @ModelAttribute("userForm") UserForm userForm,
			BindingResult bindingResult, Model model) {
		System.out.println("Opening Registeration Details Handler...");
		UserFormDetails userFormDetails = new UserFormDetails();

		userFormDetails.setTotalFamilyMembers(0);
		userFormDetails.setTotalBrothers(0);
		userFormDetails.setTotalSisters(0);

		model.addAttribute("userForm", userForm);
		model.addAttribute("userFormDetails", userFormDetails);

		return "registerdetails";
	}

	@RequestMapping("/registerdetailsbyemployee")
	public String registerationByEmp(Model model) {
		System.out.println("Opening Registeration Handler...");

		UserFormDetails userFormDetails = new UserFormDetails();

		// Fetch distinct relisions, castes categories from the database
		List<String> religions = userService.getAllDistinctReligions();
		List<String> castes = userService.getAllDistinctCastes(userFormDetails.getReligion());

		userFormDetails.setTotalFamilyMembers(0);
		userFormDetails.setTotalBrothers(0);
		userFormDetails.setTotalSisters(0);

		model.addAttribute("userFormDetails", userFormDetails);
		model.addAttribute("religions", religions);
		model.addAttribute("castes", castes);

		return "registerdetails";
	}

	@PostMapping("/do-register")
	public String processRegisteration(@Valid @ModelAttribute("userForm") UserForm userForm,
			BindingResult bindingResult, Model model, HttpSession session) {

		if (bindingResult.hasErrors()) {
			System.out.println("\n ---> processRegisteration\n" + bindingResult.toString());
			return "register";
		}

		// Check If Email is Unique, Forward to Next Registration step...
		boolean isEmailValid = userService.isEmailUnique(userForm.getEmail());
		System.out.println("1isEmailValid: " + isEmailValid);

		if (!isEmailValid) {
			System.out.println("2isEmailValid: " + isEmailValid);
			Message message = Message.builder().content("Oops! This Email is taken. Kindly use Another One")
					.type(MessageType.red).build();
			session.setAttribute("message", message);
			return "register";
		}

		System.out.println("isEmailValid=================> :" + isEmailValid);

		UserFormDetails userFormDetails = new UserFormDetails();

		session.setAttribute("userForm", userForm);

		model.addAttribute("userForm", userForm);
		model.addAttribute("userFormDetails", userFormDetails);

		// Fetch distinct relisions, castes categories from the database
		List<String> religions = userService.getAllDistinctReligions();
		List<String> castes = userService.getAllDistinctCastes(userFormDetails.getReligion());

		model.addAttribute("religions", religions);
		model.addAttribute("castes", castes);

		return "registerdetails";
	}

//  Processing for User_Registering Handler----->
	@PostMapping("/do-registerdetails")
	public String processRegisterationDetails(@Valid @ModelAttribute("userFormDetails") UserFormDetails userFormDetails,
			BindingResult bindingResult, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
			@RequestParam(value = "userImages") List<MultipartFile> userImages, Model model, HttpSession session)
			throws Exception {

		if (!agreement) {
			throw new Exception("You must agree to the terms and conditions.");
		}

		// Fetch Form-Data from UserForm to bind with Model_Object by @ModelAttribute
		System.out.println("Processing Process do-register Handler...");
//		System.out.println(userForm);

//		if (!agreement) {
//			System.out.println("You have not agreed the terms and conditions");
//			throw new Exception("You have not agreed the terms and conditions");
//		}

//		 FileValidator imageValidator = new FileValidator();
//		    imageValidator.validate(userFormDetails, bindingResult);

		// Store the uploaded files in a temporary location
		session.setAttribute("uploadedFiles", userImages);

		// validate form data
		if (bindingResult.hasErrors()) {
			System.out.println("\n ---> processRegisterationDetails\n" + bindingResult.toString());
			return "registerdetails";
		}

//		if (userImages.isEmpty() || userImages == null) {
//			System.out.println("\n ---> processRegisterationDetails\n" + userImages.toString());
//			return "registerdetails";
//			// throw new RuntimeException("File is empty");
//		}

		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm"));
		String currentDateAndTime = date + "_" + time;

		userDefaultValues.setDefaultValues(userFormDetails);

		User user = new User();
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm != null && userForm.getEmail() != null && !userForm.getEmail().isEmpty()) {
			user.setEmail(userForm.getEmail());
			user.setPassword(userForm.getPassword());
		} else {
//			Random random = new Random();
//			int randomNumber = random.nextInt(100000, 999999);
//			String userPassword = "emp@" + randomNumber;
			// If userForm is null or email is empty, generate a unique email
			String uniqueEmail = uniqueEmailGenerator.generateUniqueEmail();
			user.setEmail(uniqueEmail); // Set the generated unique email
			user.setPassword("emb@321");
		}

		user.setName(userFormDetails.getYourName());
		user.setUserCreationTime(currentDateAndTime);
		user.setGender(userFormDetails.getGender());
		user.setReligion(userFormDetails.getReligion());
		user.setCaste(userFormDetails.getCaste().trim());
		System.out.println("userFormDetails.getCaste(): " + userFormDetails.getCaste());
		System.out.println("userFormDetails.getCaste().trim(): " + userFormDetails.getCaste().trim());
		user.setSubcaste(userFormDetails.getSubcaste());

		// Getting Age from DOB...
		String dateOfBirth = userFormDetails.getDateOfBirth(); // This is in "MM/dd/yyyy"
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate dob = LocalDate.parse(dateOfBirth, inputFormatter); // Parse input date
		String formattedDateOfBirth = dob.format(outputFormatter); // Format to "dd/MM/yyyy"

		user.setDateOfBirth(formattedDateOfBirth); // Set formatted date for database storage
		int age = Period.between(dob, LocalDate.now()).getYears(); // Calculate age
		user.setAge(age); // Set age on user object

		System.out.println("--------------------------------------birthDate-------------------------------------"
				+ user.getDateOfBirth());
		System.out.println(
				"--------------------------------------Age-------------------------------------" + user.getAge());

		user.setBrithTime(userFormDetails.getBrithTime());
		user.setHeight(userFormDetails.getHeight());
		user.setMarriedStatus(userFormDetails.getMarriedStatus());
		user.setPlace(userFormDetails.getPlace());
		user.setNriPlace(userFormDetails.getNriPlace());
		user.setQualification(userFormDetails.getQualification());
		user.setQualificationField(userFormDetails.getQualificationField());
		user.setOccupation(userFormDetails.getOccupation());
		user.setYourJobTitle(userFormDetails.getYourJobTitle());
		user.setYourJobSalary(userFormDetails.getYourJobSalary());

		user.setFamilyStatus(userFormDetails.getFamilyStatus());
		user.setTotalFamilyMembers(userFormDetails.getTotalFamilyMembers());
		user.setTotalBrothers(userFormDetails.getTotalBrothers());
		user.setTotalSisters(userFormDetails.getTotalSisters());

		user.setFatherName(userFormDetails.getFatherName());
		user.setFatherOccupation(userFormDetails.getFatherOccupation());
		user.setFatherJobTitle(userFormDetails.getFatherJobTitle());
		user.setFatherJobSalary(userFormDetails.getFatherJobSalary());

		user.setMotherName(userFormDetails.getMotherName());
		user.setMotherOccupation(userFormDetails.getMotherOccupation());
		user.setMotherJobTitle(userFormDetails.getMotherJobTitle());
		user.setMotherJobSalary(userFormDetails.getMotherJobSalary());

		user.setAnyDemand(userFormDetails.getAnyDemand());
		user.setAnyRemarks(userFormDetails.getAnyRemarks());
		user.setAddress(userFormDetails.getAddress());

		user.setPhoneNumber1(userFormDetails.getPhoneNumber1());
		user.setPhoneNumber2(userFormDetails.getPhoneNumber2());

		user.setFormFilledBy(userFormDetails.getFormFilledBy());
		user.setRole(USER_ROLE.ROLE_USER);

//		List<String> userRole = new ArrayList<>();
//		userRole.add("normal");
//		user.setRoleList(userRole);

		System.out.println("--------------------------------------BrithTime-------------------------------------"
				+ user.getBrithTime());

		if (userImages != null && !userImages.isEmpty()) {
			List<String> imageUrls = imageService.uploadImages(userImages, UUID.randomUUID().toString());
			List<String> publicIds = new ArrayList<>();

			for (int i = 0; i < imageUrls.size(); i++) {
				publicIds.add(UUID.randomUUID().toString());
			}

//			user.setPicture(imageUrls.get(0)); // set the first image as the profile picture
//			user.setImages(imageUrls); // store the list of image URLs
			user.setImagesList(imageUrls); // store the list of image URLs

			System.out.println("URL-------------------");
			System.out.println(imageUrls);
			System.out.println("Public IDs-------------------");
			System.out.println(publicIds);
		}

//		 for (Iterator<String> iterator = user.getImages().iterator(); iterator.hasNext();) {
//			String string = iterator.next();
////			System.out.println(imageUrls.get());
//		}
//		 userImages.toString();

		System.out.println("\n\n --->do-registerdetails: " + userForm);
		model.addAttribute("users", user);
		model.addAttribute("userFormDetails", userFormDetails);
		User savedUser = userService.saveUser(user);
		System.out.println("user Saved 2: " + savedUser);

		// Adding Message that Register Successfully :)
		Message message = Message.builder().content("Registration Successful :)").type(MessageType.green).build();
		session.setAttribute("message", message);

		// Re-direct to Register_Page
		return "redirect:/login";
	}

}
