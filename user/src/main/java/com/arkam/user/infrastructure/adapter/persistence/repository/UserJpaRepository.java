package com.arkam.user.infrastructure.adapter.persistence.repository;

import com.arkam.user.infrastructure.adapter.persistence.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends MongoRepository<UserEntity, String> {
}
