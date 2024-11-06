package com.radovan.spring.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.radovan.spring.entity.OrderItemEntity;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Integer> {

	@Query(value = "select * from order_items where order_id = :orderId", nativeQuery = true)
	List<OrderItemEntity> listAllByOrderId(@Param("orderId") Integer orderId);
}