package com.parminder.authentication.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.parminder.authentication.bo.Table;
import com.parminder.authentication.bo.User;

public interface TableRepository  extends MongoRepository<Table, String> {
	@Query("{ 'name' : ?0 }")
	Table findByName(String email);
	
}