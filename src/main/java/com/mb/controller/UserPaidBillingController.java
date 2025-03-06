package com.mb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mb.entities.PaymentResponse;
import com.mb.entities.PhonePePayment;
import com.mb.entities.User;
import com.mb.helpers.Message;
import com.mb.helpers.MessageType;
import com.mb.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserPaidBillingController {
	@Autowired
	private EmailService emailService;

	@Value("${mb.phonenumber}")
	private String mbNumber;

	@Value("${server.baseUrl}")
	private String baseUrl;

	@Value("${ekam.email}")
	private String ekamEmail;

	public void processSendUserPaidBilling(User user, PaymentResponse paymentResponse) {
		
		String subject = "Ekam Marriage Bureau - Payment Confirmation"; 
		String message = ""
			    + "<div style='font-family: \"Segoe UI\", Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 15px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.2);'>"
			    + "  <div style='background: linear-gradient(135deg, #e47a2e, #ffcc33); padding: 20px; text-align: center;'>"
			    + "    <h1 style='color: #fff; font-size: 18px; margin: 0;'>Ekam <span style='color: #333;'>Marriage Bureau</span></h1>"
			    + "    <p style='color: #fff; font-size: 12px; margin: 5px 0 0;'>Payment Confirmation</p>"
			    + "  </div>"
			    + "  <div style='background: #f9f9f9; padding: 15px;'>"
			    + "    <h2 style='color: #333; font-size: 14px;'>&#128176; Payment Successful!</h2>"
			    + "    <p style='font-size: 12px; color: #555; line-height: 1.5;'>Dear <strong>" + user.getName() + "</strong>,</p>"
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
			    + "    <p style='font-size: 12px; color: #555;'>Thank you for your payment. Your transaction has been successfully processed. Below is your payments Details:</p>"
			    + "    <div style='background: #fff; border: 1px solid #ddd; padding: 12px; border-radius: 10px;'>"
			    + "      <table style='width: 100%; font-size: 12px; color: #555; line-height: 1.5;'>"
			    + "        <tr><td style='padding: 5px 0;'><strong>&#128100; User ID:</strong></td><td style='padding: 5px 0;'>" + paymentResponse.getUserId() + "</td></tr>"
			    + "        <tr><td style='padding: 5px 0;'><strong>&#128179; Razorpay Order ID:</strong></td><td style='padding: 5px 0;'>" + paymentResponse.getRazorpayOrderId() + "</td></tr>"
			    + "        <tr><td style='padding: 5px 0;'><strong>&#128179; Razorpay Payment ID:</strong></td><td style='padding: 5px 0;'>" + paymentResponse.getRazorpayPaymentId() + "</td></tr>"
			    + "        <tr><td style='padding: 5px 0;'><strong>&#128197; Payment Date:</strong></td><td style='padding: 5px 0;'>" + paymentResponse.getCreatedAt() + "</td></tr>"
			    + "        <tr><td style='padding: 5px 0;'><strong>&#128390; Validity Period:</strong></td><td style='padding: 5px 0;'>" + paymentResponse.getValidityPeriod() + " " + paymentResponse.getValidityType() + "</td></tr>"
			    + "      </table>"
			    + "    </div>"
			    + "    <div style='text-align: center; margin-top: 5px;'>" // This centers the button
			    + "      <a href='" + baseUrl + "' style='font-size: 16px; background-color: #e47a2e; padding: 12px 30px; color: white; text-decoration: none; border-radius: 50px; box-shadow: 0 4px 8px rgba(0,0,0,0.2); display: inline-block; transition: all 0.3s ease;'>"
			    + "        Visit Ekam Marriage Bureau"
			    + "      </a>"
			    + "    </div>"
			    + "    <p style='font-size: 12px; color: #555; margin-top: 10px;'>For any inquiries, please reach out to us via email or phone:</p>"
			    + "    <div style='display: flex; margin: 15px 0; flex-direction: row; justify-content: center; align-items: center;'>"
			    + "     <a href='tel:+91" + mbNumber + "' style='color: #e47a2e; font-size: 14px; text-decoration: none; margin: 0 10px;'>"
			    + "      <strong>&#128222;</strong> Call-Us "
			    + "     </a>"
			    + "     <a href='mailto:support@ekammarriagebureau.com' style='color: #e47a2e; font-size: 14px; text-decoration: none; margin: 0 10px;'>"
			    + "      <strong>&#9993;</strong> Email-Us"
			    + "     </a>"
			    + "    </div>"
			    + "  </div>"
			    + "  <div style='background: #333; padding: 15px; text-align: center; margin-top: -15px;'>"
			    + "    <p style='color: #fff; font-size: 10px; margin: 0;'>© 2024 Ekam Marriage Bureau. All Rights Reserved.</p>"
			    + "    <p style='color: #aaa; font-size: 10px; margin: 5px 0 0;'>Visit us at Chaura Bazaar, Mandir Wala Bazar, Gobind Nagar, Amritsar, Punjab, India</p>"
			    + "  </div>"
			    + "</div>"
			    + "<style>"
			    + "  @media (max-width: 480px) {"
			    + "    h1 { font-size: 16px; }"
			    + "    h2 { font-size: 12px; }"
			    + "    p, td { font-size: 10px; }"
			    + "    div, table { width: 100%; padding: 10px; }"
			    + "    .fa { font-size: 16px; margin-right: 5px; }"
			    + "    .fa-phone, .fa-envelope { font-size: 16px; }"
			    + "  }"
			    + "</style>";

		
		String to = user.getEmail();

		boolean flag = this.emailService.sendEmail(subject, message, to);

		if (flag) {
			this.emailService.sendEmail(subject, message, ekamEmail);
			System.out.println(user.getUserId() + ": Payment recipt Sended :)");
		} else {
			System.out.println(user.getUserId() + ": Payment recipt Not Send :(");
		}
	
	}
	
	public void processSendUserPGPaidBilling(User user, PhonePePayment phonePePayment) {
		
		String subject = "Ekam Marriage Bureau - Payment Confirmation"; 
		String message = ""
			    + "<div style='font-family: \"Segoe UI\", Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 15px; overflow: hidden; box-shadow: 0 5px 15px rgba(0,0,0,0.2);'>"
			    + "  <div style='background: linear-gradient(135deg, #e47a2e, #ffcc33); padding: 20px; text-align: center;'>"
			    + "    <h1 style='color: #fff; font-size: 18px; margin: 0;'>Ekam <span style='color: #333;'>Marriage Bureau</span></h1>"
			    + "    <p style='color: #fff; font-size: 12px; margin: 5px 0 0;'>Payment Confirmation</p>"
			    + "  </div>"
			    + "  <div style='background: #f9f9f9; padding: 15px;'>"
			    + "    <h2 style='color: #333; font-size: 14px;'>&#128176; Payment Successful!</h2>"
			    + "    <p style='font-size: 12px; color: #555; line-height: 1.5;'>Dear <strong>" + user.getName() + "</strong>,</p>"
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
			    + "    <p style='font-size: 12px; color: #555;'>Thank you for your payment. Your transaction has been successfully processed. Below is your payments Details:</p>"
			    + "    <div style='background: #fff; border: 1px solid #ddd; padding: 12px; border-radius: 10px;'>"
			    + "      <table style='width: 100%; font-size: 12px; color: #555; line-height: 1.5;'>"
			    + "        <tr><td style='padding: 5px 0;'><strong>&#128179; MerchantTransaction ID:</strong></td><td style='padding: 5px 0;'>" + phonePePayment.getMerchantTransactionId() + "</td></tr>"
			    + "        <tr><td style='padding: 5px 0;'><strong>&#128179; PhonePeTransaction ID:</strong></td><td style='padding: 5px 0;'>" + phonePePayment.getPhonePeTransactionId() + "</td></tr>"
			    + "        <tr><td style='padding: 5px 0;'><strong>&#128197; Payment Date:</strong></td><td style='padding: 5px 0;'>" + phonePePayment.getCreatedAt() + "</td></tr>"
			    + "        <tr><td style='padding: 5px 0;'><strong>&#128390; Validity Period:</strong></td><td style='padding: 5px 0;'>" + phonePePayment.getValidityPeriod() + " " + phonePePayment.getValidityType() + "</td></tr>"
			    + "      </table>"
			    + "    </div>"
			    + "    <div style='text-align: center; margin-top: 5px;'>" // This centers the button 
			    + "      <a href='" + baseUrl + "' style='font-size: 16px; background-color: #e47a2e; padding: 12px 30px; color: white; text-decoration: none; border-radius: 50px; box-shadow: 0 4px 8px rgba(0,0,0,0.2); display: inline-block; transition: all 0.3s ease;'>"
			    + "        Visit Ekam Marriage Bureau"
			    + "      </a>"
			    + "    </div>"
			    + "    <p style='font-size: 12px; color: #555; margin-top: 10px;'>For any inquiries, please reach out to us via email or phone:</p>"
			    + "    <div style='display: flex; margin: 15px 0; flex-direction: row; justify-content: center; align-items: center;'>"
			    + "     <a href='tel:+91" + mbNumber + "' style='color: #e47a2e; font-size: 14px; text-decoration: none; margin: 0 10px;'>"
			    + "      <strong>&#128222;</strong> Call-Us "
			    + "     </a>"
			    + "     <a href='mailto:support@ekammarriagebureau.com' style='color: #e47a2e; font-size: 14px; text-decoration: none; margin: 0 10px;'>"
			    + "      <strong>&#9993;</strong> Email-Us"
			    + "     </a>"
			    + "    </div>"
			    + "  </div>"
			    + "  <div style='background: #333; padding: 15px; text-align: center; margin-top: -15px;'>"
			    + "    <p style='color: #fff; font-size: 10px; margin: 0;'>© 2024 Ekam Marriage Bureau. All Rights Reserved.</p>"
			    + "    <p style='color: #aaa; font-size: 10px; margin: 5px 0 0;'>Visit us at Chaura Bazaar, Mandir Wala Bazar, Gobind Nagar, Amritsar, Punjab, India</p>"
			    + "  </div>"
			    + "</div>"
			    + "<style>"
			    + "  @media (max-width: 480px) {"
			    + "    h1 { font-size: 16px; }"
			    + "    h2 { font-size: 12px; }"
			    + "    p, td { font-size: 10px; }"
			    + "    div, table { width: 100%; padding: 10px; }"
			    + "    .fa { font-size: 16px; margin-right: 5px; }"
			    + "    .fa-phone, .fa-envelope { font-size: 16px; }"
			    + "  }"
			    + "</style>";

		
		String to = user.getEmail();

		boolean flag = this.emailService.sendEmail(subject, message, to);

		if (flag) {
			this.emailService.sendEmail(subject, message, ekamEmail);
			System.out.println(user.getUserId() + ": Payment recipt Sended :)");
		} else {
			System.out.println(user.getUserId() + ": Payment recipt Not Send :(");
		}
	
	}

}
