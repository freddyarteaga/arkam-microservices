package com.arkam.order.infrastructure.adapter.persistence;

import com.arkam.order.application.port.out.CartItemRepositoryPort;
import com.arkam.order.domain.model.CartItem;
import com.arkam.order.infrastructure.adapter.persistence.entity.CartItemEntity;
import com.arkam.order.infrastructure.adapter.persistence.mapper.CartItemPersistenceMapper;
import com.arkam.order.infrastructure.adapter.persistence.repository.CartItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartItemRepositoryAdapter implements CartItemRepositoryPort {

    private final CartItemJpaRepository cartItemJpaRepository;
    private final CartItemPersistenceMapper cartItemPersistenceMapper;

    @Override
    public Mono<CartItem> save(CartItem cartItem) {
        return Mono.fromCallable(() -> {
            CartItemEntity entity = cartItemPersistenceMapper.toEntity(cartItem);
            CartItemEntity savedEntity = cartItemJpaRepository.save(entity);
            return cartItemPersistenceMapper.toDomain(savedEntity);
        });
    }

    @Override
    public Mono<Optional<CartItem>> findByUserIdAndProductId(String userId, String productId) {
        return Mono.fromCallable(() -> cartItemJpaRepository.findByUserIdAndProductId(userId, productId)
                .map(cartItemPersistenceMapper::toDomain));
    }

    @Override
    public Flux<CartItem> findByUserId(String userId) {
        return Mono.fromCallable(() -> cartItemJpaRepository.findByUserId(userId).stream()
                .map(cartItemPersistenceMapper::toDomain)
                .collect(Collectors.toList()))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Void> delete(CartItem cartItem) {
        return Mono.fromRunnable(() -> {
            CartItemEntity entity = cartItemPersistenceMapper.toEntity(cartItem);
            cartItemJpaRepository.delete(entity);
        });
    }

    @Override
    public Mono<Void> deleteByUserId(String userId) {
        return findByUserId(userId)
                .flatMap(this::delete)
                .then();
    }
}