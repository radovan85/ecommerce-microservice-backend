package com.radovan.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.radovan.spring.entity.OrderAddressEntity;

@Repository
public interface OrderAddressRepository extends JpaRepository<OrderAddressEntity, Integer> {

}