package com.mb.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mb.entities.Testimonials;
import com.mb.entities.User;

public interface TestimonialsRepo extends JpaRepository<Testimonials, Long> {

//	@EntityGraph(attributePaths = { "review_id", "name", "rating", "review" })
//	List<Testimonials> findAll(Testimonials testimonials);


//    @EntityGraph(attributePaths = { "review_id", "name", "rating", "review" })
//    List<Testimonials> findAllWithAttributes();
    
//    List<Testimonials> findAllByOrderByNameAsc();
    List<Testimonials> findAll();
    
	Optional<Testimonials> findById(Long reviewId);


}
