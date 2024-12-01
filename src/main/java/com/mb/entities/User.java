package com.mb.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mb.domain.USER_ROLE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "app_user")
@Table(name = "app_user") // Change from user to app_user
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long userId;

	private String userCreationTime;

	// @Column(name = "user_name", nullable = false)
	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	@Getter(AccessLevel.NONE)
	private String password;

//	User Images...
//	@ElementCollection(fetch = FetchType.EAGER)

	// Store image URLs in the same table
//	@ElementCollection(fetch = FetchType.EAGER)
//	@CollectionTable(name = "user_images", joinColumns = @JoinColumn(name = "user_id")) // This maps within same table
//	@Column(name = "image_url")
//	private List<String> images = new ArrayList<>();

//	@Column(columnDefinition = "TEXT")
//	private String images; // Store comma-separated image URLs

//	@JsonIgnore
//	public List<String> getImagesList() {
//	    if (this.images == null || this.images.isEmpty()) {
//	        return new ArrayList<>();
//	    }
//	    return Arrays.asList(this.images.split(","));
//	}
//
//	@JsonIgnore
//	public void setImagesList(List<String> imagesList) {
//	    this.images = String.join(",", imagesList);
//	}

	// Store images as a single JSON string
	@Column(columnDefinition = "TEXT")
	private String images;
//	private String picture;

	// Utility method to convert a list of image URLs to a JSON string
	@JsonIgnore
	public void setImagesList(List<String> imagesList) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			// Ensure the list is properly serialized into a single JSON string
			this.images = objectMapper.writeValueAsString(imagesList);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	// Utility method to get a list of image URLs from the JSON string in the
	// database
	@JsonIgnore
	public List<String> getImagesList() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			// Convert the JSON string back to a list of image URLs
			return objectMapper.readValue(this.images, List.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public String getPicture() {
		return images != null && !images.isEmpty() ? getImagesList().get(0)
				: "https://manpreetsingh.vercel.app/_next/image?url=%2FImage-removebg.png&w=1080&q=75";
	}

//	@ElementCollection(fetch = FetchType.EAGER)
//	private List<String> CloudinaryImagePublicIds = new ArrayList<>();
//	private String cloudinaryImagePublicId;

	private String gender; // male, female

	private String religion; // hindu, sikh, muslium
	private String caste; // ramgharia, suri, jatt, bania, hindu
	private String subcaste; // Bhamra...

	private int minAge;
//	private int age;
	private int maxAge;

	private String dateOfBirth;
	private Integer age;
	private String brithTime;

	private double minHeight;
	private Double height;
	private double maxHeight;

	private String marriedStatus;
	private String place;
	private String nriPlace;

	private String qualification;
	private String qualificationField;

	private String occupation;
	private String yourJobTitle;
	private String yourJobSalary;

	private String familyStatus;
	private Integer totalFamilyMembers;
	private Integer totalBrothers;
	private Integer totalSisters;

	private String fatherName;
	private String fatherOccupation;
	private String fatherJobTitle;
	private String fatherJobSalary;

	private String motherName;
	private String motherOccupation;
	private String motherJobTitle;
	private String motherJobSalary;

	private String anyDemand;
	private String anyRemarks;
	private String address;

	private String phoneNumber1;
	private String phoneNumber2;

	private String formFilledBy;

	private USER_ROLE role; // role

	public USER_ROLE getRole() {
		return this.role;
	}

	public boolean hasRole(String roleName) {
		return this.role != null && this.role.name().equals(roleName);
	}

	public boolean hasRole(USER_ROLE user_ROLE) {
		return this.role != null && this.getRole().equals(user_ROLE);
	}

	public boolean hasAnyRole(USER_ROLE... roles) {
		return Arrays.stream(roles).anyMatch(role -> this.role == role);
	}
//	public boolean hasAnyRole(String... roles) {
//		return Arrays.stream(roles).anyMatch(role -> this.role != null && this.role.name().equals(role));
//	}

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roleList = new ArrayList<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// list of roles[USER,ADMIN]
		// Collection of SimpGrantedAuthority[roles{ADMIN,USER}]
		Collection<SimpleGrantedAuthority> roles = roleList.stream().map(role -> new SimpleGrantedAuthority(role))
				.collect(Collectors.toList());
		return roles;
	}

//	@Getter(value = AccessLevel.NONE)
//	@Setter(value = AccessLevel.NONE)
	private boolean subscriptionIsActive;

//	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
////	@JoinColumn(name = "payment_response_id")
//	@JoinColumn()
//	private PaymentResponse paymentResponse;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn()
	@PrimaryKeyJoinColumn
	private PaymentResponse paymentResponse;

	private String razorpaySignature;

	// for this project:
	// email id hai wahi hamare username
	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", name=" + name + ", email=" + email + ", password=" + password + ", images="
				+ images + ", gender=" + gender + ", religion=" + religion + ", caste=" + caste + ", subcaste="
				+ subcaste + ", minAge=" + minAge + ", maxAge=" + maxAge + ", dateOfBirth=" + dateOfBirth + ", age="
				+ age + ", brithTime=" + brithTime + ", minHeight=" + minHeight + ", height=" + height + ", maxHeight="
				+ maxHeight + ", marriedStatus=" + marriedStatus + ", place=" + place + ", nriPlace=" + nriPlace
				+ ", qualification=" + qualification + ", qualificationField=" + qualificationField + ", occupation="
				+ occupation + ", yourJobTitle=" + yourJobTitle + ", yourJobSalary=" + yourJobSalary + ", familyStatus="
				+ familyStatus + ", totalFamilyMembers=" + totalFamilyMembers + ", totalBrothers=" + totalBrothers
				+ ", totalSisters=" + totalSisters + ", fatherName=" + fatherName + ", fatherOccupation="
				+ fatherOccupation + ", fatherJobTitle=" + fatherJobTitle + ", fatherJobSalary=" + fatherJobSalary
				+ ", motherName=" + motherName + ", motherOccupation=" + motherOccupation + ", motherJobTitle="
				+ motherJobTitle + ", motherJobSalary=" + motherJobSalary + ", anyDemand=" + anyDemand + ", anyRemarks="
				+ anyRemarks + ", address=" + address + ", phoneNumber1=" + phoneNumber1 + ", phoneNumber2="
				+ phoneNumber2 + ", formFilledBy=" + formFilledBy + ", roleList=" + roleList + "]";
	}

}
