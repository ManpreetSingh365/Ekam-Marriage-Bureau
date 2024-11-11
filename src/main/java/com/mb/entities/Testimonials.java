package com.mb.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "testimonials")
@Table(name = "testimonials")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Testimonials {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long reviewId;

	private String name;
	private String review;
	private int rating;
}
