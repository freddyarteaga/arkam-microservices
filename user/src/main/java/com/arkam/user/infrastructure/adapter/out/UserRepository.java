package com.arkam.user.infrastructure.adapter.out;

import com.arkam.user.infrastructure.UserEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveMongoRepository<UserEntity, String> {
}