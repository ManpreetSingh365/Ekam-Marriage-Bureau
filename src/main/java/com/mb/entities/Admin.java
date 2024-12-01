package com.mb.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.domain.USER_ROLE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "app_admin")
@Table(name = "app_admin") // Change from user to app_user
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Admin {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long userId;


	//	@Column(name = "user_name", nullable = false)
	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	@Getter(AccessLevel.NONE)
	private String password;
	
	private USER_ROLE role; // role

	
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



}
