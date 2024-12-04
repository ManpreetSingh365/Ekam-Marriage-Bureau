package com.mb.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.mb.dto.UserProfileDto;
import com.mb.entities.User;

public interface UserService {

	User saveUser(User user);

	Optional<User> getUserById(Long id);

	List<User> getAllUsers();

	Optional<User> updateUser(User user);

	void deleteUserById(Long id);

	public void deleteUser(User user);

	boolean isUserExist(Long userId);

	boolean isUserExistByEmail(String email);

	User getUserByEmail(String email);

// 	Add More Methods here Related User Service[ Logic ]

	public List<User> findMatchUserDetails(User user);
//  public List<User> getUsersByAgeRange(int minAge, int maxAge);

	Page<User> findMatchUserDetailsByFilter(User user, int page, int size, String sortBy, String direction);

	Page<User> getByUser(int page, int size, String sortField, String sortDirection);

	public void saveFile(MultipartFile file);

	public List<String> getAllDistinctReligions();

	public List<String> getAllDistinctCastes(String religion);

	public List<String> getAllDistinctQualification();

	public List<String> getAllDistinctOccupation();

	public boolean isEmailUnique(String email);

//	public UserProfileDto getUserProfileById(Long userId);

}
