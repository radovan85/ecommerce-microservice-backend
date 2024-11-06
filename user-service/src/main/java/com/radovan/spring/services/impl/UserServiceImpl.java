package com.radovan.spring.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.exceptions.ExistingInstanceException;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repositories.RoleRepository;
import com.radovan.spring.repositories.UserRepository;
import com.radovan.spring.services.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TempConverter tempConverter;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserById(Integer id) {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepository.findById(id)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The user has not been found!")));
		return tempConverter.userEntityToDto(userEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserDto> listAllUsers() {
		// TODO Auto-generated method stub

		List<UserEntity> allUsers = userRepository.findAll();
		return allUsers.stream().map(tempConverter::userEntityToDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getUserByEmail(String email) {

		UserEntity userEntity = userRepository.findByEmail(email)
				.orElseThrow(() -> new InstanceUndefinedException(new Error("Invalid user!")));
		return tempConverter.userEntityToDto(userEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDto getCurrentUser() {
		// TODO Auto-generated method stub
		UserDto returnValue = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("Authentication: " + authentication);
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUsername = authentication.getName();
			Optional<UserEntity> userOptional = userRepository.findByEmail(currentUsername);
			if (userOptional.isPresent()) {
				returnValue = tempConverter.userEntityToDto(userOptional.get());
			} else {
				Error error = new Error("Invalid user!");
				throw new InstanceUndefinedException(error);
			}
		} else {
			Error error = new Error("Invalid user!");
			throw new InstanceUndefinedException(error);
		}

		return returnValue;
	}

	@Override
	@Transactional
	public void suspendUser(Integer userId) {
		// TODO Auto-generated method stub
		UserDto userDto = getUserById(userId);
		UserEntity userEntity = tempConverter.userDtoToEntity(userDto);
		userEntity.setEnabled((byte) 0);
		userRepository.saveAndFlush(userEntity);

	}

	@Override
	@Transactional
	public void clearSuspension(Integer userId) {
		// TODO Auto-generated method stub
		UserDto userDto = getUserById(userId);
		UserEntity userEntity = tempConverter.userDtoToEntity(userDto);
		userEntity.setEnabled((byte) 1);
		userRepository.saveAndFlush(userEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean isAdmin() {
		// TODO Auto-generated method stub
		Boolean returnValue = false;
		UserDto currentUser = getCurrentUser();
		RoleEntity roleAdmin = roleRepository.findByRole("ADMIN").orElse(null);
		if (roleAdmin != null) {
			List<Integer> rolesIds = currentUser.getRolesIds();
			if (rolesIds.contains(roleAdmin.getId())) {
				returnValue = true;
			}
		}

		return returnValue;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Authentication> authenticateUser(String username, String password) {
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
		Optional<UserEntity> userOptional = userRepository.findByEmail(username);
		return userOptional.flatMap(user -> {
			try {
				Authentication auth = authenticationManager.authenticate(authReq);
				return Optional.of(auth);
			} catch (AuthenticationException e) {
				// Handle authentication failure
				return Optional.empty();
			}
		});
	}

	@Override
	@Transactional
	public UserDto addUser(UserDto user) {
		// TODO Auto-generated method stub
		Optional<UserEntity> userOptional = userRepository.findByEmail(user.getEmail());
		if (userOptional.isPresent()) {
			throw new ExistingInstanceException(new Error("This email exists already!"));
		}
		RoleEntity roleEntity = roleRepository.findByRole("ROLE_USER")
				.orElseThrow(() -> new InstanceUndefinedException(new Error("The role has not been found!")));
		List<RoleEntity> roles = new ArrayList<>();
		roles.add(roleEntity);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled((short) 1);
		UserEntity userEntity = tempConverter.userDtoToEntity(user);
		userEntity.setRoles(roles);
		UserEntity storedUser = userRepository.save(userEntity);
		List<UserEntity> users = roleEntity.getUsers();
		if (users == null) {
			users = new ArrayList<>();
		}

		users.add(storedUser);
		roleEntity.setUsers(users);
		roleRepository.saveAndFlush(roleEntity);

		return tempConverter.userEntityToDto(storedUser);
	}

	@Override
	@Transactional
	public void deleteUser(Integer userId) {
		// TODO Auto-generated method stub
		UserDto user = getUserById(userId);
		RoleEntity roleAdmin = roleRepository.findByRole("ADMIN").orElse(null);
		if (roleAdmin != null) {
			if (!user.getRolesIds().contains(roleAdmin.getId())) {
				userRepository.deleteById(userId);
				userRepository.flush();
			}
		}
	}

}