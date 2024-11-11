package com.mb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mb.services.EmailService;
import com.mb.services.impl.EmailServiceImpl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

//	@Bean
//	public EmailService emailService() {
//		return new EmailServiceImpl(); 
//	}

//	@Bean
//	public BCryptPasswordEncoder bcryptPasswordEncoder() {
//		return new BCryptPasswordEncoder();
//	}

	@Bean
	public PasswordEncoder appPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Value("${cloudinary.cloud.name}")
	private String cloudName;

	@Value("${cloudinary.api.key}")
	private String apiKey;

	@Value("${cloudinary.api.secret}")
	private String apiSecret;

	@Bean
	public Cloudinary cloudinary() {

		return new Cloudinary(ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}