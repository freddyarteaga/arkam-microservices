package com.arkam.order.infrastructure.adapter.persistence.repository;

import com.arkam.order.infrastructure.adapter.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}