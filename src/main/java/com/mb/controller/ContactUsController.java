package com.mb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mb.helpers.Message;
import com.mb.helpers.MessageType;
import com.mb.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ContactUsController {
	@Autowired
	private EmailService emailService;
	
	@Value("${mb.phonenumber}")
	private String mbNumber;

	@Value("${ekam.email}")
	private String ekamEmail;

	@Value("${server.baseUrl}")
	private String baseUrl;


	@PostMapping("/send-contactusquery")
	public String processSendContactUsQuery(
			@RequestParam(value = "contactName", defaultValue = "Not Mentioned") String contactName,
			@RequestParam(value = "contactEmail", defaultValue = "Not Mentioned") String contactEmail,
			@RequestParam(value = "contactNumber", defaultValue = "Not Mentioned") String contactNumber,
			@RequestParam(value = "contactFormFilledBy", defaultValue = "Not Mentioned") String contactFormFilledBy,
			@RequestParam(value = "contactMessage", defaultValue = "Not Mentioned") String contactMessage,
			HttpSession session) {

				
				String subject = "Ekam Marriage Bureau - New Contact Inquiry";
				String message = ""
					    + "<div style='font-family: \"Segoe UI\", Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 20px auto; border: 1px solid #e0e0e0; border-radius: 15px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.2);'>"
					    + "  <div style='background: linear-gradient(135deg, #e47a2e, #ffcc33); padding: 15px; text-align: center;'>"
					    + "    <h1 style='color: #fff; font-size: 22px; margin: 0;'>Ekam <span style='color: #333;'>Marriage Bureau</span></h1>"
					    + "    <p style='color: #fff; font-size: 13px; margin: 5px 0 0;'>Helping You Find Your Perfect Match</p>"
					    + "  </div>"
					    + "  <div style='background: #f9f9f9; padding: 15px;'>"
					    + "    <h2 style='color: #333; font-size: 16px;'>&#128231; New Contact Inquiry</h2>"
					    + "    <p style='font-size: 13px; color: #555; line-height: 1.5;'>Dear Admin,</p>"
					    + "    <p style='font-size: 13px; color: #555;'>You have received a new inquiry through the <strong>Contact Us</strong> Form. Please review the details below:</p>"
					    + "    <div style='background: #fff; border: 1px solid #ddd; padding: 10px; border-radius: 10px;'>"
					    + "      <table style='width: 100%; font-size: 13px; color: #555; line-height: 1.6;'>"
					    + "        <tr><td style='padding: 5px 0;'><strong>&#128100; Name:</strong></td><td style='padding: 5px 0;'>" + contactName + "</td></tr>"
					    + "        <tr><td style='padding: 5px 0;'><strong>&#128231; Email:</strong></td><td style='padding: 5px 0;'>" + contactEmail + "</td></tr>"
					    + "        <tr><td style='padding: 5px 0;'><strong>&#128222; Phone Number:</strong></td><td style='padding: 5px 0;'>" + contactNumber + "</td></tr>"
					    + "        <tr><td style='padding: 5px 0;'><strong>&#9998; Filled By:</strong></td><td style='padding: 5px 0;'>" + contactFormFilledBy + "</td></tr>"
					    + "        <tr><td style='padding: 5px 0;'><strong>&#128172; Message:</strong></td><td style='padding: 5px 0;'>" + contactMessage + "</td></tr>"
					    + "      </table>"
					    + "    </div>"
					    + "    <div style='text-align: center; margin-top: 5px;'>" // This centers the button
					    + "      <a href='" + baseUrl + "/findmatch' style='font-size: 16px; background-color: #e47a2e; padding: 12px 30px; color: white; text-decoration: none; border-radius: 50px; box-shadow: 0 4px 8px rgba(0,0,0,0.2); display: inline-block; transition: all 0.3s ease;'>"
					    + "        Visit Ekam Marriage Bureau"
					    + "      </a>"
					    + "    </div>"
					    + "    <p style='margin: 15px 0 0; font-size: 11px; color: #999;'>This inquiry was automatically generated. For any questions, please contact us at <a href='mailto:support@ekammarriagebureau.com' style='color: #e47a2e;'>support@ekammarriagebureau.com</a>.</p>"
					    + "  </div>"
					    + "  <div style='background: #333; padding: 15px; text-align: center;'>"
					    + "    <p style='color: #fff; font-size: 12px; margin: 0;'>© 2024 Ekam Marriage Bureau. All Rights Reserved.</p>"
					    + "    <p style='color: #aaa; font-size: 10px; margin: 5px 0 0;'>For inquiries, visit us at Chaura Bazaar, Mandir Wala Bazar, Gobind Nagar, Amritsar, Punjab, India or call <a href='tel:+91" + mbNumber + "' style='color: #e47a2e;'>+91 " + mbNumber + "</a>.</p>"
					    + "  </div>"
					    + "</div>"
					    + "<style>"
					    + "  @media (max-width: 480px) {"
					    + "    h1 { font-size: 20px; }"
					    + "    h2 { font-size: 14px; }"
					    + "    p, td { font-size: 12px; }"
					    + "    div, table { width: 100%; padding: 10px; }"
					    + "  }"
					    + "</style>";
		String to = ekamEmail;

		boolean flag = this.emailService.sendEmail(subject, message, to);

		if (flag) {
			Message msg = Message.builder().content("Message Send Succesfully :)").type(MessageType.green).build();
			session.setAttribute("message", msg);
			return "index";

		} else {
			Message msg = Message.builder().content("Someting is Wrong !!").type(MessageType.red).build();
			session.setAttribute("message", msg);
			return "index";
		}

	}
}
