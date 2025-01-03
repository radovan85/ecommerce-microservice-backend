package com.radovan.spring.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.radovan.spring.entity.ProductImageEntity;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Integer> {

	@Query(value = "select * from product_images where product_id = :productId", nativeQuery = true)
	Optional<ProductImageEntity> findByProductId(@Param("productId") Integer productId);
}