package com.radovan.spring.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.radovan.spring.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {

	@Query(value = "select * from products where category_id = :categoryId", nativeQuery = true)
	List<ProductEntity> findAllByCategoryId(@Param("categoryId") Integer categoryId);

}
