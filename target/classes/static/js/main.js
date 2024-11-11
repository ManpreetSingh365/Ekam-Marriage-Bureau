(function($) {
	"use strict";

	// Navbar on scrolling
	$(window).scroll(function() {
		if ($(this).scrollTop() >= 0) { // set to 200
			$('.navbar').fadeIn('slow').css('display', 'flex');
		} else {
			$('.navbar').fadeOut('slow').css('display', 'none');
		}
	});


	// Smooth scrolling on the navbar links
	$(".navbar-nav a").on('click', function(event) {
		if (this.hash !== "") {
			event.preventDefault();

			$('html, body').animate({
				scrollTop: $(this.hash).offset().top - 45
			}, 1500, 'easeInOutExpo');

			if ($(this).parents('.navbar-nav').length) {
				$('.navbar-nav .active').removeClass('active');
				$(this).closest('a').addClass('active');
			}
		}
	});


	// Modal Video
	$(document).ready(function() {
		var $videoSrc;
		$('.btn-play').click(function() {
			$videoSrc = $(this).data("src");
		});
		console.log($videoSrc);

		$('#videoModal').on('shown.bs.modal', function(e) {
			$("#video").attr('src', $videoSrc + "?autoplay=1&amp;modestbranding=1&amp;showinfo=0");
		})

		$('#videoModal').on('hide.bs.modal', function(e) {
			$("#video").attr('src', $videoSrc);
		})
	});


	// Scroll to Bottom
	$(window).scroll(function() {
		if ($(this).scrollTop() > 100) {
			$('.scroll-to-bottom').fadeOut('slow');
		} else {
			$('.scroll-to-bottom').fadeIn('slow');
		}
	});


	// Portfolio isotope and filter
	var portfolioIsotope = $('.portfolio-container').isotope({
		itemSelector: '.portfolio-item',
		layoutMode: 'fitRows'
	});
	$('#portfolio-flters li').on('click', function() {
		$("#portfolio-flters li").removeClass('active');
		$(this).addClass('active');

		portfolioIsotope.isotope({ filter: $(this).data('filter') });
	});


	// Back to top button
	$(window).scroll(function() {
		if ($(this).scrollTop() > 200) {
			$('.back-to-top').fadeIn('slow');
		} else {
			$('.back-to-top').fadeOut('slow');
		}
	});
	$('.back-to-top').click(function() {
		$('html, body').animate({ scrollTop: 0 }, 1500, 'easeInOutExpo');
		return false;
	});


	$(".gallery-carousel").owlCarousel({
		autoplay: true,           // Enable autoplay
		autoplayTimeout: 2000,     // Set interval for autoplay (2 seconds here)
		smartSpeed: 1500,
		dots: false,
		loop: true,
		nav: true,
		navText: [
			'<i class="fa fa-angle-left" aria-hidden="true"></i>',
			'<i class="fa fa-angle-right" aria-hidden="true"></i>'
		],
		responsive: {
			0: { items: 1 },
			576: { items: 2 },
			768: { items: 3 },
			992: { items: 4 },
			1200: { items: 5 }
		}
	});

	/*	// Gallery carousel
		$(".gallery-carousel").owlCarousel({
			autoplay: false,
			smartSpeed: 1500,
			dots: false,
			loop: true,
			nav: true,
			navText: [
				'<i class="fa fa-angle-left" aria-hidden="true"></i>',
				'<i class="fa fa-angle-right" aria-hidden="true"></i>'
			],
			responsive: {
				0: {
					items: 1
				},
				576: {
					items: 2
				},
				768: {
					items: 3
				},
				992: {
					items: 4
				},
				1200: {
					items: 5
				}
			}
		});
	*/

})(jQuery);


// For go to Top..............................
//Get the button
var mybutton = document.getElementById("mywhatsappBtn");

// When the user scrolls down 20px from the top of the document, show the button
window.onscroll = function() { scrollFunction() };

function scrollFunction() {
	if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
		mybutton.style.display = "block";
		mybutton.classList.add('bounceIn'); // Add animation when button appears
	} else {
		mybutton.style.display = "none";
		mybutton.classList.remove('bounceIn'); // Remove animation when button is hidden
	}
}
/*// When the user clicks on the button, scroll to the top of the document
function topFunction() {
	document.body.scrollTop = 0;
	document.documentElement.scrollTop = 0;
}
*/
