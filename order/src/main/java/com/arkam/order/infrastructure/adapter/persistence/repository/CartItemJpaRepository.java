package com.arkam.order.infrastructure.adapter.persistence.repository;

import com.arkam.order.infrastructure.adapter.persistence.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemJpaRepository extends JpaRepository<CartItemEntity, Long> {
    Optional<CartItemEntity> findByUserIdAndProductId(String userId, String productId);
    List<CartItemEntity> findByUserId(String userId);

    @Modifying
    @Query("DELETE FROM CartItemEntity c WHERE c.userId = :userId")
    void deleteByUserId(String userId);
}