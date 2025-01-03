package com.radovan.spring.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.dto.RoleDto;
import com.radovan.spring.dto.UserDto;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.entity.UserEntity;
import com.radovan.spring.repositories.RoleRepository;
import com.radovan.spring.repositories.UserRepository;

@Component
public class TempConverter {

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	public UserDto userEntityToDto(UserEntity userEntity) {
		UserDto returnValue = mapper.map(userEntity, UserDto.class);
		returnValue.setEnabled((short) userEntity.getEnabled());
		Optional<List<RoleEntity>> rolesOptional = Optional.ofNullable(userEntity.getRoles());
		List<Integer> rolesIds = new ArrayList<Integer>();

		if (!rolesOptional.isEmpty()) {
			rolesOptional.get().forEach((roleEntity) -> {
				rolesIds.add(roleEntity.getId());
			});
		}

		returnValue.setRolesIds(rolesIds);

		return returnValue;
	}

	public UserEntity userDtoToEntity(UserDto userDto) {
		UserEntity returnValue = mapper.map(userDto, UserEntity.class);
		returnValue.setEnabled(userDto.getEnabled().byteValue());
		List<RoleEntity> roles = new ArrayList<>();
		Optional<List<Integer>> rolesIdsOptional = Optional.ofNullable(userDto.getRolesIds());

		if (!rolesIdsOptional.isEmpty()) {
			rolesIdsOptional.get().forEach((roleId) -> {
				RoleEntity roleEntity = roleRepository.findById(roleId).orElse(null);
				if (roleEntity != null) {
					roles.add(roleEntity);
				}

			});
		}

		returnValue.setRoles(roles);

		return returnValue;
	}

	public RoleDto roleEntityToDto(RoleEntity roleEntity) {
		RoleDto returnValue = mapper.map(roleEntity, RoleDto.class);
		Optional<List<UserEntity>> usersOptional = Optional.ofNullable(roleEntity.getUsers());
		List<Integer> userIds = new ArrayList<>();

		if (!usersOptional.isEmpty()) {
			usersOptional.get().forEach((user) -> {
				userIds.add(user.getId());
			});
		}

		returnValue.setUsersIds(userIds);
		return returnValue;
	}

	public RoleEntity roleDtoToEntity(RoleDto roleDto) {
		RoleEntity returnValue = mapper.map(roleDto, RoleEntity.class);
		Optional<List<Integer>> usersIdsOptional = Optional.ofNullable(roleDto.getUsersIds());
		List<UserEntity> users = new ArrayList<>();

		if (!usersIdsOptional.isEmpty()) {
			usersIdsOptional.get().forEach((userId) -> {
				UserEntity userEntity = userRepository.findById(userId).get();
				users.add(userEntity);
			});
		}

		returnValue.setUsers(users);
		return returnValue;
	}
}
