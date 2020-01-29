
package com.parminder.authentication.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.parminder.authentication.bo.Comment;

public interface CommentRepository  extends MongoRepository<Comment, String> {
	@Query("{ 'entityId' : ?0 , 'entityClass': ?1}")
	List<Comment> findByEntityIdAndEntityClass(String entityId,String entityClass);
	
		
}
