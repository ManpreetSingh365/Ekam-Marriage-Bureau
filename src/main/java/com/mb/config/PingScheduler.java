package com.mb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PingScheduler {

	private final RestTemplate restTemplate;

	@Value("${server.baseUrl}")
	private String BASE_URL;

	private static final Logger logger = LoggerFactory.getLogger(PingScheduler.class);

	// Constructor Injection
	@Autowired
	public PingScheduler(@Lazy RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Scheduled(fixedRateString = "${server.ActiveTime:6000000}") // default to 10 min if not set
	public void ping() {
		
//		String url = "http://" + BASE_URL + "/ping";
		String url = BASE_URL + "/ping";
		logger.info("Pinging application at {}", url); // Add this line
		try {
			String response = restTemplate.getForObject(url, String.class);
			logger.info("Ping response: {}", response);
			System.out.println("inside: try ping");
		} catch (Exception e) {
			logger.error("Error pinging the application: {}", e.getMessage());
			System.out.println("inside: catch ping");
		}
	}
}