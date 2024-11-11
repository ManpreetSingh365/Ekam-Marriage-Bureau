package com.mb.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mb.entities.Testimonials;
import com.mb.entities.User;
import com.mb.helpers.Helper;
import com.mb.helpers.Message;
import com.mb.helpers.MessageType;
import com.mb.services.TestimonialsService;
import com.mb.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TestimonialController {

	@Autowired
	private UserService userService;

	@Autowired
	private TestimonialsService testimonialsService;

	@Value("${admin.email}")
	private String adminEmail;

	@RequestMapping("/testimonials")
	public String Testimonials(Model model, Authentication authentication) {

		List<Testimonials> testimonialsAll = testimonialsService.getAllTestimonials();
		// Reverse the list
		Collections.reverse(testimonialsAll);

		Optional<Authentication> authOptional = Optional.ofNullable(authentication);
		model.addAttribute("authOptional", authOptional.isPresent());
		System.out.println("authOptional.isPresent(): " + authOptional.isPresent());

		if (authOptional.isPresent()) {
			String email = Helper.getEmailOfLoggedInUser(authOptional.get());
			model.addAttribute("email", email);
			model.addAttribute("adminEmail", adminEmail);
			System.out.println("email: " + email);
			System.out.println("adminEmail: " + adminEmail);
		}

		model.addAttribute("testimonials", testimonialsAll);

		return "testimonials";
	}

	@PostMapping("/do-testimonials")
	public String processTestimonials(@RequestParam("name") String name, @RequestParam("review") String review,
			@RequestParam(value = "rating", defaultValue = "5") int rating, Model model, HttpSession session) {

//		if (bindingResult.hasErrors()) {
//			System.out.println("\n ---> processRegisteration\n" + bindingResult.toString());
//			return "Testimonials";
//		}

		Testimonials testimonials = new Testimonials();

		testimonials.setName(name);
		testimonials.setReview(review);
		testimonials.setRating(rating);

		Testimonials savedTestimonials = testimonialsService.saveTestimonials(testimonials);

		// Adding Message that Register Successfully :)
		Message message = Message.builder().content("Your Review Add Successful :)").type(MessageType.green).build();
		session.setAttribute("message", message);

		return "redirect:/testimonials";
	}

	@GetMapping("/do-deletetestimonial/{reviewId}")
	public String deleteUserByAdmin(@PathVariable("reviewId") Long reviewId, Model model, HttpSession session) {
	    System.out.println("deleteUser Handler..........");

	    testimonialsService.deleteTestimonialsById(reviewId);

	    // Adding Message that User Deleted Successfully :)
	    Message message = Message.builder()
	                             .content("Testimonial Successfully Deleted by Admin :)")
	                             .type(MessageType.green)
	                             .build();
	    session.setAttribute("message", message);

	    return "redirect:/testimonials";
	}
}
