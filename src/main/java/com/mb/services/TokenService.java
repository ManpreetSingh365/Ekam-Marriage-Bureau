package com.mb.services;

public interface TokenService {
	public String generateToken(Long userId);

	public boolean isValidToken(Long userId, String token);

}
