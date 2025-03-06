package com.mb.entities;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "phone_pe_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores extra fields in JSON
public class PhonePePayment { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for MySQL, or SEQUENCE for PostgreSQL
    private Long id; // Primary Key

    @Column(name = "merchant_transaction_id", nullable = false)
    @JsonProperty("merchantTransactionId")
    private String merchantTransactionId;

    @Column(name = "phone_pe_transaction_id", unique = true)
    private String phonePeTransactionId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, SUCCESS, FAILED

    private String validityPeriod;
    private String validityType;

//    @Column(name = "app_user_id", nullable = false) // Ensure foreign key is correctly mapped
    @Column(name = "app_user_id", insertable = false, updatable = false)
    private Long appUserId;

    @ManyToOne
//    @JoinColumn(name = "app_user_id", referencedColumnName = "userId", insertable = false, updatable = false)
    @JoinColumn(name = "app_user_id", referencedColumnName = "userId")
    private User user; 

//    @Column(name = "created_at", nullable = false, updatable = false)
//    private Timestamp createdAt;
//
//    @Column(name = "expiry_date")
//    private Date expiryDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "UTC")
    private Timestamp createdAt;

    @Column(name = "expiry_date", nullable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "UTC")
    @JsonInclude(JsonInclude.Include.NON_NULL) // Ignore expiryDate if null
    private Date expiryDate;
    
	public boolean isSubscriptionValid() {
		if (createdAt == null) {
			return false; // or throw an exception, depending on your requirements
		}
		int validityPeriod = Integer.parseInt(this.validityPeriod);
		String validityType = this.validityType;

		System.out
				.println("<===============================: isSubscriptionValid :==================================>");
		System.out.println("validityPeriod: " + validityPeriod);
		System.out.println("validityType: " + validityType);

		// Calculate the expiry date based on the validity period and type
		Calendar expiryDate = Calendar.getInstance();
		expiryDate.setTimeInMillis(this.createdAt.getTime());
		if (validityType.equalsIgnoreCase("days")) {
			expiryDate.add(Calendar.DAY_OF_YEAR, validityPeriod);
		} else if (validityType.equalsIgnoreCase("months")) {
			expiryDate.add(Calendar.MONTH, validityPeriod);
		} else if (validityType.equalsIgnoreCase("years")) {
			expiryDate.add(Calendar.YEAR, validityPeriod);
		}
		System.out.println("=====>expiryDate.getTime(): " + expiryDate.getTime() + " | new Date(): " + new Date());

		this.expiryDate = expiryDate.getTime();

		// Check if the current date is before the expiry date
		return new Date().before(expiryDate.getTime());
	}
}
