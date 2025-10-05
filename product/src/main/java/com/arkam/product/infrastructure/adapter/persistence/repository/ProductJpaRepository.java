package com.arkam.product.infrastructure.adapter.persistence.repository;

import com.arkam.product.infrastructure.adapter.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByActiveTrue();

    @Query("SELECT p FROM ProductEntity p WHERE p.active = true AND p.stockQuantity > 0 AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ProductEntity> searchProducts(@Param("keyword") String keyword);

    Optional<ProductEntity> findByIdAndActiveTrue(Long id);
}