package com.mb.services.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mb.entities.PaymentResponse;
import com.mb.entities.PhonePePayment;
import com.mb.repositories.PhonePePaymentRepository;

import jakarta.transaction.Transactional;
import lombok.Data;

@Service
public class PhonePePaymentService {
	@Autowired
	private PhonePePaymentRepository phonePePaymentRepository;

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public PhonePePaymentService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional
	public void createPaymentRecord(PhonePePayment phonePePayment) {
		String query = "INSERT INTO phone_pe_payment "
				+ "(merchant_transaction_id, phone_pe_transaction_id, app_user_id, amount, status, created_at, "
				+ "expiry_date, validity_period, validity_type) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplate.update(query, phonePePayment.getMerchantTransactionId(), phonePePayment.getPhonePeTransactionId(),
				phonePePayment.getAppUserId(), phonePePayment.getAmount(), phonePePayment.getStatus(),
				phonePePayment.getCreatedAt(), phonePePayment.getExpiryDate(), phonePePayment.getValidityPeriod(),
				phonePePayment.getValidityType());
	}

	@Transactional
    public void updatePaymentStatus(PhonePePayment phonePePayment) {
        String query = "UPDATE phone_pe_payment SET "
                + "merchant_transaction_id = ?, phone_pe_transaction_id = ?, app_user_id = ?, amount = ?, status = ?, "
                + "created_at = ?, expiry_date = ?, validity_period = ?, validity_type = ? "
                + "WHERE id = ?";

        jdbcTemplate.update(query,
                phonePePayment.getMerchantTransactionId(),
                phonePePayment.getPhonePeTransactionId(),
                phonePePayment.getAppUserId(),
                phonePePayment.getAmount(),
                phonePePayment.getStatus(),
                phonePePayment.getCreatedAt() != null ? new Timestamp(phonePePayment.getCreatedAt().getTime()) : null,
                phonePePayment.getExpiryDate() != null ? new Timestamp(phonePePayment.getExpiryDate().getTime()) : null,
                phonePePayment.getValidityPeriod(),
                phonePePayment.getValidityType(),
                phonePePayment.getId()
        );
    }
	
	public PhonePePayment getPaymentByTransactionId(String transactionId) {
		return phonePePaymentRepository.findByMerchantTransactionId(transactionId).orElse(null);
	}

	public PhonePePayment getPaymentByAppUserId(Long appUserId) {
		return phonePePaymentRepository.findByAppUserId(appUserId).orElse(null);
	}

	//	@Transactional
//	public void updatePhonePePayment(PhonePePayment phonePePayment) {
//
//		System.out.println("Before: updation: ----->");
//		System.out.println(phonePePayment);
//
//		phonePePaymentRepository.save(phonePePayment);
//
//		System.out.println("💾 Payment status updated in phone_pe_payment: " + phonePePayment.getId());
//	}
//
//	public void updatePaymentStatus(PhonePeWebhookRequest request) {
//		Optional<PhonePePayment> paymentOptional = phonePePaymentRepository
//				.findByMerchantTransactionId(request.merchantTransactionId());
//		if (paymentOptional.isPresent()) {
//			PhonePePayment payment = paymentOptional.get();
//			payment.setStatus(request.status());
//			payment.setPhonePeTransactionId(request.transactionId());
//			phonePePaymentRepository.save(payment);
//			System.out.println("Payment updated successfully.");
//		} else {
//			System.err.println("Payment not found for Transaction ID: " + request.merchantTransactionId());
//		}
//	}

//	public void storePaymentResponse(Long userId, PhonePePayment phonePePayment) {
//		String query = "INSERT INTO payments (user_id, razorpay_payment_id, razorpay_order_id, razorpay_signature, validity_period, validity_type) VALUES (?, ?, ?, ?, ?, ?)";
//		jdbcTemplate.update(query, userId, phonePePayment.get, paymentResponse.getRazorpayOrderId(),
//				paymentResponse.getRazorpaySignature(), paymentResponse.getValidityPeriod(),
//				paymentResponse.getValidityType());
//	}

//	@Transactional
//	public void updatePhonePePayment(String merchantTransactionId, String status, String phonePeTransactionId) {
//		PhonePePayment payment = phonePePaymentRepository.findByMerchantTransactionId(merchantTransactionId)
//				.orElseThrow(() -> new RuntimeException("Payment not found: " + merchantTransactionId));
//
//		payment.setStatus(status);
//		payment.setPhonePeTransactionId(phonePeTransactionId); // Save PhonePe TXN ID
////        payment.setUpdatedAt(LocalDateTime.now()); // Optional timestamp update
//		phonePePaymentRepository.save(payment);
//
//		System.out.println("💾 Payment status updated in phone_pe_payment: " + merchantTransactionId);
//	}
}
