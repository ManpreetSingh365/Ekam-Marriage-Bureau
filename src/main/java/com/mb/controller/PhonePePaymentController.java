package com.mb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.entities.PhonePePayment;
import com.mb.entities.User;
import com.mb.repositories.UserRepo;
import com.mb.services.UserService;
import com.mb.services.impl.PhonePePaymentService;
import com.mb.services.impl.PhonePeWebhookRequest;

import lombok.Data;
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
			request.setStatus("PENDING");
			request.setCreatedAt(new Timestamp(System.currentTimeMillis()));

			// ✅ Generate a unique merchantTransactionId
			String merchantTransactionId = "MTX" + System.currentTimeMillis();
			System.out.println("Generated merchantTransactionId: " + merchantTransactionId); //
			request.setMerchantTransactionId(merchantTransactionId);

			// ✅ Generate a unique PhonePe transaction ID
			String phonePeTransactionId = "TRANS" + System.currentTimeMillis();
			System.out.println("Generated phonePeTransactionId ID: " + phonePeTransactionId); // TRANS1741013200362
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
		requestData.put("merchantUserId", request.getAppUserId()); // Fixed issue here
		requestData.put("amount", (int) (request.getAmount() * 100)); // Convert to paise
		requestData.put("redirectUrl", redirectUrl);
		requestData.put("redirectMode", "REDIRECT");
		requestData.put("callbackUrl", callbackUrl);
		requestData.put("mobileNumber", "1234567890");

		Map<String, String> instrument = Map.of("type", "PAY_PAGE");
		requestData.put("paymentInstrument", instrument);

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
		User user = (User) authentication.getPrincipal(); // Get the authenticated user
		System.out.println("Before-Before: user.isSubscriptionIsActive(): " + user.isSubscriptionIsActive());

		if (phonePePayment == null || phonePePayment.getMerchantTransactionId() == null) {
			System.err.println("❌ Error: Invalid Payment Data Received");
			response.put("error", "Invalid Payment Data");
			return ResponseEntity.badRequest().body(response);
		}

		System.out.println("✅ Webhook Received: " + phonePePayment);
// ✅ Webhook Received: PhonePePayment(id=1, merchantTransactionId=TESTPGPAYUATX, phonePeTransactionId=TRANS1741175336866, amount=1.0, status=SUCCESS, validityPeriod=30, validityType=days, appUserId=3, user=null, createdAt=2025-03-05 22:48:56.866, expiryDate=Fri Apr 04 22:48:56 IST 2025)
// ✅ Webhook Received: PhonePePayment(id=1, merchantTransactionId=TESTPGPAYUATX, phonePeTransactionId=TRANS1741253222873, amount=1.0, status=SUCCESS, validityPeriod=30, validityType=days, appUserId=1, user=null, createdAt=2025-03-06 20:27:02.872, expiryDate=Sat Apr 05 20:27:02 IST 2025)

		// Update the existing payment record with the webhook details
		if ("SUCCESS".equalsIgnoreCase(phonePePayment.getStatus())) {
			System.out.println("Before: user.isSubscriptionIsActive(): " + user.isSubscriptionIsActive());
			user.setSubscriptionIsActive(true);
			user.setPhonePePayments(Collections.singletonList(phonePePayment));

			System.out.println("After: user.isSubscriptionIsActive(): " + user.isSubscriptionIsActive());

			userService.saveUser(user);

			phonePePayment.setPhonePeTransactionId(phonePePayment.getPhonePeTransactionId());
			phonePePayment.setValidityPeriod(phonePePayment.getValidityPeriod());
			phonePePayment.setValidityType(phonePePayment.getValidityType());
			if (phonePePayment.isSubscriptionValid()) {
				phonePePayment.setExpiryDate(phonePePayment.getExpiryDate());
			}


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

	@GetMapping("/status/{appUserId}/{validityPeriod}/{validityType}")
	public ResponseEntity<Map<String, String>> checkPaymentStatus(@PathVariable Long appUserId,
			@PathVariable String validityPeriod, @PathVariable String validityType) {
		Map<String, String> response = new HashMap<>();

		System.out.println("Inside checkPaymentStatus method --->");
		// Fetch payment status from your database
		PhonePePayment payment = paymentService.getPaymentByAppUserId(appUserId);

		System.out.println("payment: " + payment);

		if (payment == null) {
			response.put("status", "NOT_FOUND");
			System.out.println("Status Not Found --->");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		System.out.println("After: Inside checkPaymentStatus method --->");
		payment.setValidityPeriod(validityPeriod);
		payment.setValidityType(validityType);
		if (payment.isSubscriptionValid()) {
			payment.setExpiryDate(payment.getExpiryDate());
		}

		// ✅ Format expiryDate properly before adding to response
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String formattedExpiryDate = (payment.getExpiryDate() != null) ? formatter.format(payment.getExpiryDate())
				: null;

		response.put("id", String.valueOf(payment.getId()));
		response.put("appUserId", String.valueOf(payment.getAppUserId()));
		response.put("status", "SUCCESS");
		response.put("merchantTransactionId", String.valueOf(merchantId));
		response.put("phonePeTransactionId", String.valueOf(payment.getPhonePeTransactionId()));
		response.put("validityPeriod", String.valueOf(payment.getValidityPeriod()));
		response.put("validityType", String.valueOf(payment.getValidityType()));
		response.put("amount", String.valueOf(payment.getAmount()));
		response.put("createdAt", formatter.format(payment.getCreatedAt())); // ✅ Format createdAt
		response.put("expiryDate", formattedExpiryDate); // ✅ Format expiryDate

		System.out.println("After: payment: " + payment);

		return ResponseEntity.ok(response);
	}
}
