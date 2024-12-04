package com.mb.services.impl;

import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.mb.services.TokenService;
@Service
public class TokenServiceImpl implements TokenService {

	// Simulating a token storage (you could use a real database or cache in
	// production)
	private Map<String, Long> tokenStore = new HashMap<>();

	// Generate a token for a given userId
	public String generateToken(Long userId) {
		String token = UUID.randomUUID().toString();
		tokenStore.put(token, userId);
		return token;
	}

	// Validate if the provided token is valid for a given userId
	public boolean isValidToken(Long userId, String token) {
		Long storedUserId = tokenStore.get(token);
		return storedUserId != null && storedUserId.equals(userId);
	}
}
