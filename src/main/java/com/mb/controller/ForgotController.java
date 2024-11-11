package com.mb.controller;

import java.util.Random;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mb.repositories.UserRepo;
import com.mb.entities.User;
import com.mb.helpers.Message;
import com.mb.helpers.MessageType;
import com.mb.services.EmailService;
import com.mb.services.UserService;

@Controller
public class ForgotController {
	Random random = new Random(1000);

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserService userService;

//	@Autowired
//	private BCryptPasswordEncoder bcrypt;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Open Email_id Form Handler.....
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}

//  Processing for Sending OTP Handler----->
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		System.out.println("EMAIL " + email);

		// Check If Email is Unique, Forward to Next Registration step...
		boolean isEmailValid = userService.isEmailUnique(email);
		if (isEmailValid) {
			Message message = Message.builder().content("Oops! This Email is Invalid (Not in our DB)")
					.type(MessageType.red).build();
			session.setAttribute("message", message);
			return "forgot_email_form";
		}

		// Generating OTP of 4 digit
		int otp = random.nextInt(999999);

		System.out.println("OTP " + otp);

		// Write Code for Send OTP to Email...
		String subject = "OTP From SCM";
		String message = "" + "<div style='border:1px solid #e2e2e2; padding:20px'>" + "<h1>" + "OTP is " + "<b>" + otp
				+ "</n>" + "</h1> " + "</div>";
		String to = email;

		boolean flag = this.emailService.sendEmail(subject, message, to);

		if (flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";

		} else {
			session.setAttribute("message", "Check your email id !!");

			return "forgot_email_form";
		}
	}

	// Processing to Verify OTP Handler----->
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
		int myOtp = (int) session.getAttribute("myotp");

		System.out.println("User OTP " + otp);
		System.out.println("Our OTP " + myOtp);

		String email = (String) session.getAttribute("email");
		if (myOtp == otp) {
			// password change form
			User user = this.userRepo.getUserByUserName(email);

			if (user == null) {
				// send error message
				session.setAttribute("message", "User does not exits with this email !!");

				return "forgot_email_form";
			} else {
				// send change password form

			}

			return "password_change_form";
		} else {
			session.setAttribute("message", "You have entered wrong otp !!");
			return "verify_otp";
		}
	}

	// Processing to Change Password Handler ----->
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
		String email = (String) session.getAttribute("email");
		User user = this.userRepo.getUserByUserName(email);
		user.setPassword(this.passwordEncoder.encode(newpassword));
		this.userRepo.save(user);
		return "redirect:/login?change=password changed successfully..";

	}

}
