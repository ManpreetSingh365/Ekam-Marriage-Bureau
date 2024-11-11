package com.mb.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ping {

	@GetMapping("/ping")
	public String ping() {
		System.out.println("inside: pingCall");
		return "pong";
	}
}