package com.radovan.spring.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.security.auth.login.CredentialNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.RoleDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.DataNotValidatedException;
import com.radovan.spring.services.RoleService;
import com.radovan.spring.services.UserService;
import com.radovan.spring.utils.AuthenticationRequest;
import com.radovan.spring.utils.JwtUtil;

@RestController
public class MainController {

	@Autowired
	private UserService userService;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private RoleService roleService;

	private List<String> serviceList = Stream.of("order-service", "customer-service", "cart-service", "product-service")
			.collect(Collectors.toList());

	@PostMapping(value = "/addUser")
	public ResponseEntity<UserDto> createUser(@Validated @RequestBody UserDto user, Errors errors) {
		if (errors.hasErrors()) {
			Error error = new Error("The data has not been validated");
			throw new DataNotValidatedException(error);
		}

		UserDto storedUser = userService.addUser(user);
		return new ResponseEntity<>(storedUser, HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<UserDto> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {
		Optional<Authentication> authOptional = userService.authenticateUser(authenticationRequest.getUsername(),
				authenticationRequest.getPassword());

		if (authOptional.isEmpty()) {
			throw new CredentialNotFoundException("Invalid username or password!");
		}

		UserDto userDto = userService.getUserByEmail(authenticationRequest.getUsername());
		final UserEntity userDetails = tempConverter.userDtoToEntity(userDto);

		final String jwt = jwtTokenUtil.generateToken(userDetails);

		UserDto authUser = tempConverter.userEntityToDto(userDetails);
		authUser.setAuthToken(jwt);

		return new ResponseEntity<>(authUser, HttpStatus.OK);

	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/allUsers")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		return new ResponseEntity<>(userService.listAllUsers(), HttpStatus.OK);
	}

	@GetMapping(value = "/currentUser")
	public ResponseEntity<UserDto> getCurrentUser() {
		return new ResponseEntity<>(userService.getCurrentUser(), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@GetMapping(value = "/userDetails/{userId}")
	public ResponseEntity<UserDto> getUserDetails(@PathVariable("userId") Integer userId) {
		return new ResponseEntity<UserDto>(userService.getUserById(userId), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@DeleteMapping(value = "/deleteUser/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable("userId") Integer userId,
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService) {
		if (!"customer-service".equals(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}
		userService.deleteUser(userId);
		return new ResponseEntity<String>("The user with id " + userId + " has been removed!", HttpStatus.OK);
	}

	@GetMapping(value = "/userData/{username}")
	public ResponseEntity<UserDto> getUserDetails(@PathVariable("username") String username,
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService) {
		if (!serviceList.contains(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(userService.getUserByEmail(username), HttpStatus.OK);
	}

	@GetMapping(value = "/roles/{userId}")
	public ResponseEntity<List<RoleDto>> getUserRoles(@PathVariable("userId") Integer userId,
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService) {

		if (!serviceList.contains(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(roleService.listAllByUserId(userId), HttpStatus.OK);
	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/suspendUser/{userId}")
	public ResponseEntity<Void> suspendUser(
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService,
			@PathVariable("userId") Integer userId) {
		if (!"customer-service".equals(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}
		userService.suspendUser(userId);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@PreAuthorize(value = "hasAuthority('ADMIN')")
	@PutMapping(value = "/reactivateUser/{userId}")
	public ResponseEntity<Void> reactivateUser(
			@RequestHeader(value = "X-Source-Service", required = false) String sourceService,
			@PathVariable("userId") Integer userId) {
		if (!"customer-service".equals(sourceService)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
		}
		userService.clearSuspension(userId);
		return new ResponseEntity<>(HttpStatus.OK);

	}

}
