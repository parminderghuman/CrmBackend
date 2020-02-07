package com.parminder.authentication.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.TablePermission;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.chat.ChatParticipants;


@RestController
public class TablePermissionController {
	@Autowired
	MongoTemplate mongoTemplate;

	@GetMapping(path = "/System_Table_Permissions/{chatId}")
	public TablePermission getCommets(@PathVariable String chatId) {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		return  mongoTemplate.findOne(new Query().addCriteria(Criteria.where("parentId").is(new ObjectId(loggerInUser.getParent_id())).and("classId").is(new ObjectId(chatId))),TablePermission.class, "System_Table_Permissions");
	}
	@PostMapping(path = "/System_Table_Permissions/{chatId}")
	public TablePermission getCommets(@PathVariable String chatId,@RequestBody TablePermission tablePermission) {
		tablePermission.setClassId(new ObjectId(chatId));
	
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		TablePermission oTablePermission =   mongoTemplate.findOne(new Query().addCriteria(Criteria.where("parentId").is(new ObjectId(loggerInUser.getParent_id())).and("classId").is(new ObjectId(chatId))), TablePermission.class,	"System_Table_Permissions");
		if(oTablePermission!= null) {	
			tablePermission.set_id(oTablePermission.get_id());
		}
		return mongoTemplate.save(tablePermission,"System_Table_Permissions");
	}
}
