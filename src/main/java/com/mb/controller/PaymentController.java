package com.mb.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import com.mb.entities.PaymentResponse;
import com.mb.entities.User;
import com.mb.helpers.Helper;
import com.mb.repositories.PaymentResponseRepo;
import com.mb.repositories.UserRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.mb.services.PaymentService;
import com.mb.services.UserService;
import java.sql.Timestamp;
import com.mb.controller.UserPaidBillingController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class PaymentController {

	@Autowired
	private UserService userService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private UserPaidBillingController userPaidBillingController;

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	
	@PostMapping("/create_order")
//	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) throws Exception {
		int amt = Integer.parseInt(data.get("amount").toString());

		RazorpayClient razorpayClient = new RazorpayClient("rzp_test_PceptBJlOEtHdK", "CAscuG55h1rc4skqP9wZzL27");

		JSONObject options = new JSONObject();
		options.put("amount", amt * 100); // in paise
		options.put("currency", "INR");
		options.put("receipt", "txn_123456");

		Order order = razorpayClient.Orders.create(options);

		return order.toString();
	}

	@PostMapping("/payment-response")
	public ResponseEntity<Map<String, String>> storePaymentResponse(@RequestBody PaymentResponse paymentResponse,
			Authentication authentication) {
		Map<String, String> response = new HashMap<>();

		try {
//	        String secretKey = System.getenv("RAZORPAY_SECRET_KEY"); // Use environment variable for secret key
			String secretKey = "CAscuG55h1rc4skqP9wZzL27";

			if (paymentService.verifyPaymentSignature(paymentResponse, secretKey)) {
				User user = (User) authentication.getPrincipal(); // Get the authenticated user
				Long userId = user.getUserId(); // Get the user ID

				// Update the paymentResponse in the User entity
				user.setPaymentResponse(paymentResponse);
				user.setRazorpaySignature(paymentResponse.getRazorpaySignature());

				paymentResponse.setUserId(userId); // Set the user ID on the payment response
				paymentResponse.setCreatedAt(new Timestamp(System.currentTimeMillis())); // Set the createdAt timestamp

				paymentService.storePaymentResponse(userId, paymentResponse);

				if (paymentResponse.isSubscriptionValid()) {
					user.setSubscriptionIsActive(true);
					paymentResponse.setExpiryDate(paymentResponse.getExpiryDate());
					userPaidBillingController.processSendUserPaidBilling(user, paymentResponse);
					response.put("redirect", "true");
					response.put("redirectUrl", "/paidSuccessfully");
				} else {
					user.setSubscriptionIsActive(false);
					response.put("redirect", "false");
				}

				// Save the updated User entity
				userService.saveUser(user);

				return ResponseEntity.ok(response);
			} else {
				response.put("redirect", "false");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		} catch (Exception e) {
			// Log the exception and return an error response
			logger.error("Error storing payment response", e);
			response.put("redirect", "false");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}