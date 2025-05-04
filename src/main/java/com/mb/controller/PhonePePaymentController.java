package com.mb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.entities.PhonePePayment;
import com.mb.entities.User;
import com.mb.services.UserService;
import com.mb.services.impl.PhonePePaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
//@CrossOrigin(origins = "https://mercury-t2.phonepe.com") // Allow specific domain
//@CrossOrigin(origins = "*")  // Allow all origins
public class PhonePePaymentController {

	@Value("${phonepe.base-url}")
	private String baseUrl;

	@Value("${phonepe.merchant-id}")
	private String merchantId;

	@Value("${phonepe.secret-key}")
	private String secretKey;

	@Value("${phonepe.salt-key-index}")
	private String saltKeyIndex;

	@Value("${phonepe.redirect-url}")
	private String redirectUrl;

	@Value("${phonepe.callback-url}")
	private String callbackUrl;

	@Autowired
	private UserPaidBillingController userPaidBillingController;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RestTemplate restTemplate = new RestTemplate();
	private final PhonePePaymentService paymentService;

	@Autowired
	private UserService userService;

	@PostMapping("/initiate")
	public ResponseEntity<?> initiatePayment(@RequestBody PhonePePayment request) {
		try {
			// Set initial status and createdAt timestamp for pending payment
			request.setAppUserId(request.getAppUserId());
			request.setAmount(request.getAmount());
			request.setValidityPeriod(request.getValidityPeriod());
			request.setValidityType(request.getValidityType());
			request.setStatus("PENDING");
			request.setCreatedAt(new Timestamp(System.currentTimeMillis()));

			if (request.isSubscriptionValid()) {
				request.setExpiryDate(request.getExpiryDate());
			}

			// ✅ Generate a unique merchantTransactionId
			String merchantTransactionId = "MTX" + System.currentTimeMillis();
			request.setMerchantTransactionId(merchantTransactionId);

			// ✅ Generate a unique PhonePe transaction ID
			String phonePeTransactionId = "TRANS" + System.currentTimeMillis();
			request.setPhonePeTransactionId(phonePeTransactionId);

			// Store a pending payment record in the database
			paymentService.createPaymentRecord(request);

			Map<String, Object> payload = createPaymentRequest(request);
			String apiEndpoint = "/pg/v1/pay";
			String xVerifyHeader = generateSignature(payload, apiEndpoint);
			String paymentUrl = baseUrl + apiEndpoint;

			System.out.println("🔹 Sending payment request to: " + paymentUrl);
			System.out.println("🔹 X-VERIFY Header: " + xVerifyHeader);

			return sendPhonePeRequest(paymentUrl, payload, xVerifyHeader);
		} catch (Exception e) {
			System.err.println("❌ Payment initiation failed: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Payment initiation failed", "details", e.getMessage()));
		}
	}

	private Map<String, Object> createPaymentRequest(PhonePePayment request) throws Exception {
		Map<String, Object> requestData = new LinkedHashMap<>();
		requestData.put("merchantId", merchantId);
		requestData.put("merchantTransactionId", request.getMerchantTransactionId());
		requestData.put("appUserId", request.getAppUserId()); // Fixed issue here
		requestData.put("amount", (int) (request.getAmount() * 100)); // Convert to paise
		requestData.put("phonePeTransactionId", request.getPhonePeTransactionId());
		requestData.put("status", request.getStatus());
		requestData.put("validityPeriod", request.getValidityPeriod());
		requestData.put("validityType", request.getValidityType());
		requestData.put("createdAt", request.getCreatedAt());
		requestData.put("expiryDate", request.getExpiryDate());
		requestData.put("redirectUrl", redirectUrl);
		requestData.put("redirectMode", "REDIRECT");
		requestData.put("callbackUrl", callbackUrl);
		requestData.put("mobileNumber", "1234567890");

		request.setMerchantTransactionId(request.getMerchantTransactionId());
		request.setAppUserId(request.getAppUserId());
		request.setAmount(request.getAmount());
		request.setPhonePeTransactionId(request.getPhonePeTransactionId());
		request.setStatus(request.getStatus());
		request.setValidityPeriod(request.getValidityPeriod());
		request.setValidityType(request.getValidityType());
		request.setCreatedAt(request.getCreatedAt());
		request.setExpiryDate(request.getExpiryDate());

		Map<String, String> instrument = Map.of("type", "PAY_PAGE");
		requestData.put("paymentInstrument", instrument);

		paymentService.updatePaymentStatus(request);

		String jsonPayload = objectMapper.writeValueAsString(requestData);
		String base64Payload = Base64.getEncoder().encodeToString(jsonPayload.getBytes(StandardCharsets.UTF_8));

		System.out.println("🔹 JSON Payload: " + jsonPayload);
		System.out.println("🔹 Base64 Encoded Payload: " + base64Payload);

		return Map.of("request", base64Payload);
	}

	private String generateSignature(Map<String, Object> payload, String apiEndpoint) throws Exception {
		String base64Payload = (String) payload.get("request");
		String dataToSign = base64Payload + apiEndpoint + secretKey;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(dataToSign.getBytes(StandardCharsets.UTF_8));
		String hexHash = bytesToHex(hash);

		return hexHash + "###" + saltKeyIndex;
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder hexString = new StringBuilder(2 * bytes.length);
		for (byte b : bytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private ResponseEntity<?> sendPhonePeRequest(String url, Map<String, Object> payload, String xVerifyHeader) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("X-VERIFY", xVerifyHeader);
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));

			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

			System.out.println("✅ PhonePe Response: " + response.getBody());
			return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
		} catch (Exception e) {
			System.err.println("❌ Payment request failed: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Payment request failed", "details", e.getMessage()));
		}
	}

	// Webhook: Called automatically by PhonePe when payment status updates
	@PostMapping("/webhook")
	public ResponseEntity<Map<String, String>> handlePaymentWebhook(@RequestBody PhonePePayment phonePePayment,
			Authentication authentication) {
		Map<String, String> response = new HashMap<>();

		System.out.println("🔹 Inside handlePaymentWebhook method --->");
		System.out.println("Webhook Payment: " + phonePePayment);
		User user = (User) authentication.getPrincipal(); // Get the authenticated user

		if (phonePePayment == null || phonePePayment.getMerchantTransactionId() == null) {
			System.err.println("❌ Error: Invalid Payment Data Received");
			response.put("error", "Invalid Payment Data");
			return ResponseEntity.badRequest().body(response);
		}

		// Update the existing payment record with the webhook details
		if ("SUCCESS".equalsIgnoreCase(phonePePayment.getStatus())) {
			System.out.println("Before: user.isSubscriptionIsActive(): " + user.isSubscriptionIsActive());
			user.setSubscriptionIsActive(true);
			user.setPhonePePayments(Collections.singletonList(phonePePayment));

			System.out.println("After: user.isSubscriptionIsActive(): " + user.isSubscriptionIsActive());

			userService.saveUser(user);

			phonePePayment.setAppUserId(phonePePayment.getAppUserId());

			try {
				paymentService.updatePaymentStatus(phonePePayment);
				userPaidBillingController.processSendUserPGPaidBilling(user, phonePePayment);
			} catch (Exception e) {
				e.printStackTrace();
			}

			response.put("status", "SUCCESS");

			return ResponseEntity.ok(response);
		} else {
			user.setSubscriptionIsActive(false);
			paymentService.updatePaymentStatus(phonePePayment);
			response.put("status", "PENDING");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@GetMapping("/status/{appUserId}")
	public ResponseEntity<Map<String, String>> checkPaymentStatus(HttpSession session, @PathVariable Long appUserId) {
		Map<String, String> response = new HashMap<>();

		System.out.println("Inside checkPaymentStatus method --->");
		// Fetch payment status from your database
		PhonePePayment payment = paymentService.getPaymentByAppUserId(appUserId);

		if (payment == null) {
			response.put("status", "NOT_FOUND");
			System.out.println("Status Not Found --->");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		// ✅ Format expiryDate properly before adding to response
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String formattedExpiryDate = (payment.getExpiryDate() != null) ? formatter.format(payment.getExpiryDate())
				: null;

		response.put("id", String.valueOf(payment.getId()));
		response.put("appUserId", String.valueOf(payment.getAppUserId()));
		response.put("status", "SUCCESS");
		response.put("merchantTransactionId", String.valueOf(payment.getMerchantTransactionId()));
		response.put("phonePeTransactionId", String.valueOf(payment.getPhonePeTransactionId()));
		response.put("validityPeriod", String.valueOf(payment.getValidityPeriod()));
		response.put("validityType", String.valueOf(payment.getValidityType()));
		response.put("amount", String.valueOf(payment.getAmount()));
		response.put("createdAt", formatter.format(payment.getCreatedAt())); // ✅ Format createdAt
		response.put("expiryDate", formattedExpiryDate); // ✅ Format expiryDate

		return ResponseEntity.ok(response);
	}

//	 @PostMapping("/callback")
//	    public ResponseEntity<String> handlePhonePeCallback(@RequestBody Map<String, Object> callbackData) {
//	        System.out.println("Received PhonePe Callback: " + callbackData);
//
//	        try {
//	            String transactionId = (String) callbackData.get("transactionId");
//	            String merchantTransactionId = (String) callbackData.get("merchantTransactionId");
//	            String status = (String) callbackData.get("code"); // 'PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'PAYMENT_PENDING'
//
//	            // Process the transaction status accordingly
//	            if ("PAYMENT_SUCCESS".equals(status)) {
//	                System.out.println("Payment successful for MerchantTransactionId: " + merchantTransactionId);
//	                // Update the order/payment status in DB
//	            } else if ("PAYMENT_FAILED".equals(status)) {
//	                System.out.println("Payment failed for MerchantTransactionId: " + merchantTransactionId);
//	                // Handle payment failure
//	            } else {
//	                System.out.println("Payment status pending for MerchantTransactionId: " + merchantTransactionId);
//	                // Handle pending status
//	            }
//
//	            return ResponseEntity.ok("Callback processed successfully");
//
//	        } catch (Exception e) {
//	            System.out.println("Error processing PhonePe callback: " + e.getMessage());
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing callback");
//	        }
//	    }
}
