package com.mb.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mb.repositories.UserRepo;

@Service
public class SecurityCustomUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Apne user ko Load Karana hai...
		return userRepo.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + username));
	}
}
