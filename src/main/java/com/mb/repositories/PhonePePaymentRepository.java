package com.mb.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mb.entities.PhonePePayment;

@Repository
public interface PhonePePaymentRepository extends JpaRepository<PhonePePayment, Long> {
	
	Optional<PhonePePayment> findById(Long userId);

	List<PhonePePayment> findAll();

    Optional<PhonePePayment> findByMerchantTransactionId(String merchantTransactionId);

	void save(Optional<PhonePePayment> payment);

	Optional<PhonePePayment> findByAppUserId(Long transactionId);
}
