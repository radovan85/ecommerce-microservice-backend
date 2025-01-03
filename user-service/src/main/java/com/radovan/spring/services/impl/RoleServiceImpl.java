package com.radovan.spring.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.RoleDto;
import com.radovan.spring.entity.RoleEntity;
import com.radovan.spring.repositories.RoleRepository;
import com.radovan.spring.services.RoleService;
import com.radovan.spring.services.UserService;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private TempConverter tempConverter;

	@Override
	public List<RoleDto> listAllByUserId(Integer userId) {
		// TODO Auto-generated method stub
		userService.getUserById(userId);
		List<RoleEntity> allRoles = roleRepository.findAllByUserId(userId);
		return allRoles.stream().map(tempConverter::roleEntityToDto).collect(Collectors.toList());
	}

}
