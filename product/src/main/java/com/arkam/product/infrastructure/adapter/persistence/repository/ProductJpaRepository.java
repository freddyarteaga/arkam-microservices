package com.arkam.product.infrastructure.adapter.persistence.repository;

import com.arkam.product.infrastructure.adapter.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByActiveTrue();
    List<ProductEntity> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);
}
