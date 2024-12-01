package com.mb.controller;

import java.util.Random;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

	@Value("${mb.phonenumber}")
	private String mbNumber;

	
	@Value("${server.baseUrl}")
	private String baseUrl;

//	@Autowired
//	private BCryptPasswordEncoder bcrypt;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${ekam.email}")
	private String ekamEmail;

	// Open Email_id Form Handler.....
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}

//  Processing for Sending OTP Handler----->
	@GetMapping("/send-otp")
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
		
		User user = this.userService.getUserByEmail(email);

		// Generating OTP of 4 digit
		int otp = random.nextInt(999999);

		System.out.println("OTP " + otp);

		// Write Code for Send OTP to Email...
		String subject = "Ekam Marriage Bureau - OTP";
		String message = ""
			    + "<div style='max-width: 600px; margin: 20px auto; font-family: Arial, sans-serif; border: 1px solid #e2e2e2; border-radius: 15px; box-shadow: 0 5px 20px rgba(0,0,0,0.2); overflow: hidden;'>"
			    + "  <style>"
			    + "    @media only screen and (max-width: 600px) {"
			    + "      .user-info {"
			    + "        display: flex;"
			    + "        flex-direction: column;"
			    + "        align-items: center;"
			    + "        text-align: center;"
			    + "      }"
			    + "      .user-info img {"
			    + "        margin-bottom: 15px;"
			    + "      }"
			    + "      .user-info table {"
			    + "        width: 100%;"
			    + "      }"
			    + "      .user-info td {"
			    + "        display: block;"
			    + "        width: 100%;"
			    + "        border: none;"
			    + "        padding: 5px 0;"
			    + "      }"
			    + "    }"
			    + "  </style>"
			    + "  <!-- Header Section -->"
			    + "  <div style='background: linear-gradient(135deg, #e47a2e, #ff9900); padding: 20px; text-align: center;'>"
			    + "    <h1 style='color: #fff; font-size: 28px; margin: 0; font-weight: bold;'>Ekam Marriage Bureau</h1>"
			    + "    <p style='color: #fff; font-size: 16px; margin: 5px 0;'>Your Trusted Matchmaking Partner</p>"
			    + "  </div>"
			    + "  <!-- User Details Section -->"
			    + "  <div class='user-info' style='padding: 20px; background-color: #fff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>"
			    + "    <h2 style='color: #333; font-size: 24px; text-align: center; margin-bottom: 20px;'>User Details</h2>"
			    + "    <center> <img src='" + (user.getPicture() != null ? user.getPicture() : "https://res.cloudinary.com/dcrlfty5k/image/upload/v1729153915/yjllp8ag6uab4gdq7hog.png") + "' alt='User Picture' style='width: 80px; height: 80px; border-radius: 50%;'> </center>"
			    + "    <table style='width: 100%; border-collapse: collapse;'>"
			    + "      <tr style='background-color: #f7f7f7;'>"
			    + "        <td colspan='2' style='padding: 10px 15px; font-size: 16px; color: #333; border: 1px solid #ddd;'>"
			    + "          <strong style='font-size: 18px; color: #e47a2e;'>User ID:</strong> " + user.getUserId() + "<br>"
			    + "          <strong style='font-size: 18px; color: #e47a2e;'>Name:</strong> " + user.getName() + "<br>"
			    + "          <strong style='font-size: 18px; color: #e47a2e;'>Phone:</strong> " + user.getPhoneNumber1() + "<br>"
			    + "          <strong style='font-size: 18px; color: #e47a2e;'>Email:</strong> " + user.getEmail() + "<br>"
			    + "        </td>"
			    + "      </tr>"
			    + "    </table>"
			    + "  </div>"
			    + "  <!-- OTP Section -->"
			    + "  <div style='padding: 20px; background: #e47a2e; margin: 0; text-align: center; color: #fff;'>"
			    + "    <h2 style='margin: 10px 0; font-size: 22px;'>Your OTP Code</h2>"
			    + "    <p style='font-size: 36px; font-weight: bold; letter-spacing: 5px; margin: 10px 0;'>" + otp + "</p>"
			    + "    <p style='font-size: 14px; margin-top: 10px;'>Please use this code within 10 minutes to complete your verification.</p>"
			    + "  </div>"
			    + "  <!-- Footer Section -->"
			    + "  <div style='padding: 15px; margin-top:-10px; background: #333; color: #fff; text-align: center; font-size: 12px;'>"
			    + "    <p style='margin: 5px 0;'>Need help? Contact us:</p>"
			    + "    <p style='margin: 0;'>"
			    + "      <a href='tel:+91" + mbNumber + "' style='color: #ff9900; text-decoration: none;'><strong>&#128222;</strong> Call Us</a> | "
			    + "      <a href='mailto:support@ekammarriagebureau.com' style='color: #ff9900; text-decoration: none;'><strong>&#9993;</strong> Email Us</a>"
			    + "    </p>"
			    + "    <p style='margin: 5px 0;'>© 2024 Ekam Marriage Bureau. All rights reserved.</p>"
			    + "  </div>"
			    + "</div>";

		String to = email;

		boolean flag = this.emailService.sendEmail(subject, message, to);

		if (flag) {
			// Also send OTP on our Support Team
			this.emailService.sendEmail(subject, message, ekamEmail);
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
