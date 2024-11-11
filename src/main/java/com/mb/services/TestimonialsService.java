package com.mb.services;

import java.util.List;
import java.util.Optional;

import com.mb.entities.Testimonials;

public interface TestimonialsService {
	Optional<Testimonials> getTestimonialsById(Long id);

	List<Testimonials> getAllTestimonials();

	Testimonials saveTestimonials(Testimonials testimonials);

	void deleteTestimonialsById(Long id);

//	Optional<Testimonials> updateTestimonials(Testimonials testimonials);
//	public void deleteTestimonials(Testimonials testimonials);

}
