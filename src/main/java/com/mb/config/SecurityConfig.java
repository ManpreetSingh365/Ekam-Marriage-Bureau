package com.mb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import com.mb.services.impl.SecurityCustomUserDetailService;

import com.mb.entities.User;
import com.mb.forms.*;

@Configuration
public class SecurityConfig {

// Spring Security uses User Details Service. Whenever Login, fetch User from User Details 'Service' | In having Service using Method named as 'M:LoadUserByUsername' And then 'LoadedUser' & our 'Own_User' check if Password Match tends Proceed to Login_Page | If User having in Memory, then we have Method named as 'C:inMemoryUserDetailsManager' which implements [ I:UserDetailsManager extends I:UserDetailsService , I:UserDetailsPasswordService ]

// Manually Create User and Login using Java Code with InMemoryService Method --->
//	@Bean
//	public UserDetailsService userDetailsService() {
//
//		UserForm user1 = User.withDefaultPasswordEncoder().username("admin").password("admin123")
//				.roles("ADMIN", "USER").build();
//
//		UserForm user2 = User.withDefaultPasswordEncoder().username("user").password("user123")
//				// .roles(null)
//				.build();
//
//		var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(user1, user2);
//		return inMemoryUserDetailsManager;
//	}

	@Autowired
	private SecurityCustomUserDetailService userDetailService;

//	@Autowired
//	private OAuthAuthenicationSuccessHandler oAuthAuthenicationSuccessHandler;

	// Get User from Database via Configuration of Authentication Provider for
	// Spring Security
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		// User_Detail_Service Object
		daoAuthenticationProvider.setUserDetailsService(userDetailService);
		// Password_Encoder Object
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		// Configuration: URLs Configure who are remain public Route (or) private Route
		// based on User_Role
		httpSecurity.authorizeHttpRequests(authorize -> {
			// authorize.requestMatchers("/home", "/register", "/services").permitAll();
			authorize.requestMatchers("user/**").authenticated();
//			data-th-href="@{'/user/'+*{userId}}"
			authorize.anyRequest().permitAll();
		});

		// httpSecurity.formLogin: we'll use Form_Login in this Project
		// Form Default-Login: If want to Change anything, Change here: Related to
		// Form_Login
		// httpSecurity.formLogin(Customizer.withDefaults());
		httpSecurity.formLogin(formLogin -> {
			formLogin.loginPage("/login");
			formLogin.loginProcessingUrl("/authenticate");
			formLogin.successForwardUrl("/user/logginprofile");
			// formLogin.successForwardUrl("/user/findmatch");
			// formLogin.failureForwardUrl("/login?error=true");
			// formLogin.defaultSuccessUrl("/home");
			formLogin.usernameParameter("email");
			formLogin.passwordParameter("password");

		});

//		Disable AbstractHttpConfigurer, ByDefault is ON Provided by Spring Security...
		httpSecurity.csrf(AbstractHttpConfigurer::disable);

		httpSecurity.logout(logoutForm -> {
			logoutForm.logoutUrl("/do-logout");
			logoutForm.logoutSuccessUrl("/login?logout=true");
		});

		return httpSecurity.build();

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
