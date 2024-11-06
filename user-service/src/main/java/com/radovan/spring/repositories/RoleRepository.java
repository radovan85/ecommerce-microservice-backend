package com.radovan.spring.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.radovan.spring.entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

	Optional<RoleEntity> findByRole(String role);

	@Query(value = "SELECT r.* FROM roles r " + "JOIN users_roles ur ON r.id = ur.roles_id "
			+ "WHERE ur.user_id = :userId", nativeQuery = true)
	List<RoleEntity> findAllByUserId(@Param("userId") Integer userId);
}
