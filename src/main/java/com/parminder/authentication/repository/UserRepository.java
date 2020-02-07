package com.parminder.authentication.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.parminder.authentication.bo.User;


@Repository
public interface UserRepository extends MongoRepository<User, String> {
	@Query("{ 'username' : ?0 }")
	User findByUsername(String username);
}
