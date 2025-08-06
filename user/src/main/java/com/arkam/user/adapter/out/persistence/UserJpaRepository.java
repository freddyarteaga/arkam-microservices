package com.arkam.user.adapter.out.persistence;


import com.arkam.user.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends MongoRepository<User, String> {
}
