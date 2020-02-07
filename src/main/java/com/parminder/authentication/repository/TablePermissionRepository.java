package com.parminder.authentication.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.parminder.authentication.bo.Table;
import com.parminder.authentication.bo.TablePermission;

public interface TablePermissionRepository extends MongoRepository<TablePermission, ObjectId> {
	@Query("{ 'name' : ?0 }")
	Table findByName(String email);
	
	@Query("{ 'parentClass' : ?0 }")
	List<Table> findByParentClass(String parentClass);
	@Query("{ 'classId' : ?0 , 'parentId' : ?1}")
	TablePermission findByClassIdAndParentId(ObjectId objectId, ObjectId objectId2);
}
