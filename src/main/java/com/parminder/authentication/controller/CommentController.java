package com.parminder.authentication.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.Column;
import com.parminder.authentication.bo.Comment;
import com.parminder.authentication.bo.Comment.CommenType;
import com.parminder.authentication.bo.Permissions;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.User.UserType;
import com.parminder.authentication.repository.CommentRepository;
import com.parminder.authentication.repository.TableRepository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

@RestController
public class CommentController {

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@GetMapping(path = "/comment/{enityId}/{entityClass}")
	public List<Comment> getCommets(@PathVariable String enityId, @PathVariable String entityClass,@RequestParam HashMap<String, Object> searchCreteria,
			HttpServletRequest request) {

		LookupOperation lookupOperation = LookupOperation.newLookup().from("users").localField("commentBy")
				.foreignField("_id").as("users");
		AggregationOperation match = Aggregation.match(
				Criteria.where("entityId").is(new ObjectId(enityId)).and("entityClass").is(new ObjectId(entityClass)));
		AggregationOperation sort = Aggregation.sort(Sort.by(Order.desc("createdAt")));
		Aggregation agg = Aggregation.newAggregation(

				match, lookupOperation, sort);
		List<Comment> aggResults = mongoTemplate.aggregate(agg, "System_Comments", Comment.class).getMappedResults();
		return aggResults;
	}

	@PostMapping(path = "/comment/{enityId}/{entityClass}")
	public Comment CreateTable(@PathVariable String enityId, @PathVariable String entityClass,@RequestBody Comment comment)
			throws Exception {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		comment.setCommentBy(new ObjectId(loggerInUser.get_id()));
		comment.setCommenType(CommenType.text);
		comment.setEntityId(new ObjectId(enityId));
		comment.setEntityClass(new ObjectId(entityClass));
		comment.setCreatedAt(new Date());
		commentRepository.save(comment);
		return comment;
	}

}
