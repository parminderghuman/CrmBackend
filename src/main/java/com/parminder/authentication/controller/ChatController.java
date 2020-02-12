package com.parminder.authentication.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.chat.Chat;
import com.parminder.authentication.bo.chat.ChatParticipants;
import com.parminder.authentication.bo.chat.ChatParticipants.ChatType;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort.Order;

@RestController
public class ChatController {

	@Autowired
	MongoTemplate mongoTemplate;

	@GetMapping(path = "/System_Chats")
	public List<ChatParticipants> getCommets(@RequestParam HashMap<String, Object> searchCreteria) {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		String loggedInUserId = loggerInUser.getActiveCompany().get("_id")+"";
		LookupOperation lookupOperation1 = LookupOperation.newLookup().from("System_Chats").localField("chatId")
				.foreignField("_id").as("chats");
		LookupOperation lookupOperation3 = LookupOperation.newLookup().from("system_tables").localField("chats.entityClass")
				.foreignField("_id").as("entities");
		LookupOperation lookupOperation = LookupOperation.newLookup().from("User").localField("recipientId")
				.foreignField("_id").as("users");
		LookupOperation lookupOperationM = LookupOperation.newLookup().from("System_Messages")
				.localField("lastMessageID").foreignField("_id").as("messages");
		List<Order> orderList = new ArrayList<Sort.Order>();
		if (searchCreteria.containsKey("sort")) {
			try {
				JSONObject sort = new JSONObject(searchCreteria.get("sort").toString());
				for (String so : sort.keySet()) {
					if ("asc".equals(sort.getString(so))) {
						orderList.add(Order.asc(so));
					} else {
						orderList.add(Order.desc(so));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Criteria c = Criteria.where("userId").is(new ObjectId(loggedInUserId));
		if (searchCreteria.containsKey("query")) {
			try {
				JSONObject sort = new JSONObject(searchCreteria.get("query").toString());
				for (String so : sort.keySet()) {
					Object val = sort.get(so);
					if(val instanceof JSONObject) {
						JSONObject b  = new JSONObject(val.toString());
						for(String h : b.keySet()) {
							if(h.equals("$oid")){
								c.and(so ).is(new ObjectId(b.getString(h)));
							}else if(h.equals("$gt")){
								c.and(so ).gt(new Date(b.getLong(h)));
							}else if(h.equals("$lt")){
								c.and(so ).lt(new Date(b.getLong(h)));
							}
						}
					}else {
						c.and(so ).is(val);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		
		AggregationOperation sort = Aggregation.sort(Sort.by(orderList));
		AggregationOperation limit =  Aggregation.limit(5);

		AggregationOperation match = Aggregation
				.match(c);
		Aggregation agg = Aggregation.newAggregation(match,sort, limit,lookupOperation, lookupOperation1,lookupOperation3, lookupOperationM );

		List<ChatParticipants> aggResults = mongoTemplate
				.aggregate(agg, "System_Chat_Particpants", ChatParticipants.class).getMappedResults();
		return aggResults;	
	}

	@GetMapping(path = "/System_Chats/{chatid}")
	public Chat getChat(@PathVariable String chatid) {
		
		LookupOperation lookupOperation = LookupOperation.newLookup().from("System_Chat_Particpants").localField("_id")
				.foreignField("chatId").as("tempParticipants");

		LookupOperation lookupOperation1 = LookupOperation.newLookup().from("User")
				.localField("tempParticipants.userId").foreignField("_id").as("tempUsers");
		AggregationOperation match = Aggregation.match(Criteria.where("_id").is(new ObjectId(chatid)));
		AggregationOperation sort = Aggregation.sort(Sort.by(Order.desc("createdAt")));
		Aggregation agg = Aggregation.newAggregation(

				match, lookupOperation, lookupOperation1, sort);

		List<Chat> aggResults = mongoTemplate.aggregate(agg, "System_Chats", Chat.class).getMappedResults();
		return aggResults.get(0);
	}
	@GetMapping(path = "/System_Chats/Entity/{entityId}/{entityClass}")
	public Chat getChat(@PathVariable String entityId,@PathVariable String entityClass) {
		//User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		LookupOperation lookupOperation = LookupOperation.newLookup().from("System_Chat_Particpants").localField("_id")
				.foreignField("chatId").as("tempParticipants");

		LookupOperation lookupOperation1 = LookupOperation.newLookup().from("User")
				.localField("tempParticipants.userId").foreignField("_id").as("tempUsers");
		AggregationOperation match = Aggregation.match(Criteria.where("entityId").is(new ObjectId(entityId)).and("entityClass").is(new ObjectId(entityClass)));
		AggregationOperation sort = Aggregation.sort(Sort.by(Order.desc("createdAt")));
		Aggregation agg = Aggregation.newAggregation(

				match, lookupOperation, lookupOperation1, sort);

		List<Chat> aggResults = mongoTemplate.aggregate(agg, "System_Chats", Chat.class).getMappedResults();
		return aggResults.get(0);
	}

	@PostMapping(path = "/System_Chats/{userId}")
	public Chat CreateTable(@PathVariable String userId) {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		String loggedInUserId = loggerInUser.getActiveCompany().get("_id")+"";
//		Query query = new Query();
//		query.addCriteria(
//				Criteria.where("participants").elemMatch(
//				Criteria.where("id").in(new ObjectId(loggerInUser.get_id()),new ObjectId(userId))
//				).and("type").is(1)
//				);
//		List<Chat> chats = mongoTemplate.find(query,ChatParticipants.class, "System_Chat_Particpants");
		// if(chats == null || chats.size()==0) {

		Query query = new Query();
		query.addCriteria(Criteria.where("userId").is(new ObjectId(loggedInUserId)).and("recipientId")
				.is(new ObjectId(userId)));
		List<ChatParticipants> chatParList = mongoTemplate.find(query, ChatParticipants.class,
				"System_Chat_Particpants");
		if (chatParList != null && chatParList.size() > 0) {
			// chatParList = new ArrayList<ChatParticipants>();
//			Chat c = mongoTemplate.findOne(new Query().addCriteria(Criteria.where("_id").is( chatParList.get(0).getChatId())),Chat.class);
//			
//			
//			
//			List<ChatParticipants> cl = mongoTemplate.find(new Query().addCriteria(Criteria.where("chatId").is(new ObjectId(c.getId()))),ChatParticipants.class, "System_Chat_Particpants");
//			c.setParticipants(cl);
//			return c;
		} else {
			chatParList = new ArrayList<ChatParticipants>();

			Chat c = new Chat();
			c.setType(ChatType.OneToOne);
			c.setCreatedAt(new Date());
			c.setUpdatedAt(c.getUpdatedAt());
			c = mongoTemplate.save(c, "System_Chats");
			ChatParticipants chatParticipants = new ChatParticipants();
			chatParticipants.setUserId(new ObjectId(loggedInUserId));
			chatParticipants.setStatus(true);
			chatParticipants.setChatId(c.getId());
			chatParticipants.setChatType(ChatType.OneToOne);
			chatParticipants.setRecipientId(new ObjectId(userId));

			ChatParticipants shatParticipants = new ChatParticipants();
			shatParticipants.setUserId(new ObjectId(userId));
			shatParticipants.setStatus(true);
			shatParticipants.setChatId(c.getId());
			shatParticipants.setChatType(ChatType.OneToOne);
			shatParticipants.setRecipientId(new ObjectId(loggedInUserId));
			mongoTemplate.save(chatParticipants, "System_Chat_Particpants");
			mongoTemplate.save(shatParticipants, "System_Chat_Particpants");
			chatParList.add(chatParticipants);
			chatParList.add(shatParticipants);
			// return c;
		}
		LookupOperation lookupOperation = LookupOperation.newLookup().from("System_Chat_Particpants").localField("_id")
				.foreignField("chatId").as("tempParticipants");

		LookupOperation lookupOperation1 = LookupOperation.newLookup().from("User")
				.localField("tempParticipants.userId").foreignField("_id").as("tempUsers");
		
		AggregationOperation sort = Aggregation.sort(Sort.by(Order.desc("createdAt")));
		AggregationOperation match = Aggregation.match(Criteria.where("_id").is(chatParList.get(0).getChatId()));
		
		Aggregation agg = Aggregation.newAggregation(

				match, sort,lookupOperation, lookupOperation1 );

		List<Chat> aggResults = mongoTemplate.aggregate(agg, "System_Chats", Chat.class).getMappedResults();
		return aggResults.get(0);
	}

	public Chat createEntityChat(String name, ObjectId entityClass, ObjectId entityId, List<ObjectId> loggerInUsers) {

		Query query = new Query();
		query.addCriteria(Criteria.where("entityClass").is(entityClass).and("entityId").is(entityId));

		Chat c = mongoTemplate.findOne(query, Chat.class, "System_Chats");
		if (c == null) {

			c = new Chat();
			c.setType(ChatType.Entity);
			c.setCreatedAt(new Date());
			c.setUpdatedAt(c.getUpdatedAt());
			c.setEntityClass(entityClass);
			c.setName(name);
			c.setEntityId(entityId);
			c = mongoTemplate.save(c, "System_Chats");
		} else {
			c.setName(name);
			c = mongoTemplate.save(c, "System_Chats");
		}

		for (ObjectId loggerInUser : loggerInUsers) {
			query = new Query();
			query.addCriteria(Criteria.where("chatId").is(c.getId()).and("userId").is(loggerInUser));
			ChatParticipants chatParticipants = mongoTemplate.findOne(query, ChatParticipants.class,
					"System_Chat_Particpants");
			if (chatParticipants == null) {
				chatParticipants = new ChatParticipants();
				chatParticipants.setUserId(loggerInUser);
				chatParticipants.setStatus(true);
				chatParticipants.setChatId(c.getId());
				chatParticipants.setChatType(ChatType.Entity);
				mongoTemplate.save(chatParticipants, "System_Chat_Particpants");
			}
		}
		// chatParticipants.setRecipientId(new ObjectId(userId));
		return c;
	}

}
