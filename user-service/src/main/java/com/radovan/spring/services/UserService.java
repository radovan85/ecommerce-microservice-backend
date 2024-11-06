package com.radovan.spring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.radovan.spring.dto.UserDto;

public interface UserService {

	UserDto getUserById(Integer id);

	List<UserDto> listAllUsers();

	UserDto getUserByEmail(String email);

	UserDto getCurrentUser();

	void suspendUser(Integer userId);

	void clearSuspension(Integer userId);

	Boolean isAdmin();

	Optional<Authentication> authenticateUser(String username, String password);

	UserDto addUser(UserDto user);
	
	void deleteUser(Integer userId);
}
