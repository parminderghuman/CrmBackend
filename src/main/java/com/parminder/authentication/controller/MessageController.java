package com.parminder.authentication.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.chat.ChatParticipants;
import com.parminder.authentication.bo.chat.MessageParticipants;
import com.parminder.authentication.bo.chat.Message;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort.Order;

@RestController

public class MessageController {

	@Autowired
	MongoTemplate mongoTemplate;

	
	@GetMapping(path = "/System_Messages/{chatId}")
	public List<MessageParticipants> getMessages(@PathVariable String chatId){
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		String loggedInUserId = loggerInUser.getActiveCompany().get("_id")+"";

		LookupOperation lookupOperation = LookupOperation.newLookup().from("System_Messages").localField("messageId")
				.foreignField("_id").as("messages");	
		LookupOperation lookupOperation1 = LookupOperation.newLookup().from("User").localField("messages.messageBy")
				.foreignField("_id").as("users");
		AggregationOperation match = Aggregation.match(
				Criteria.where("chatId").is(new ObjectId(chatId)).and("userId").is(new ObjectId(loggedInUserId)));
		AggregationOperation sort = Aggregation.sort(Sort.by(Order.desc("createdAt")));
		Aggregation agg = Aggregation.newAggregation(
				match, lookupOperation, lookupOperation1,sort);
		//Document d = mongoTemplate.aggregate(agg, "messageParticipants", MessageParticipants.class).getRawResults();
		List<MessageParticipants> aggResults = mongoTemplate.aggregate(agg, "messageParticipants", MessageParticipants.class).getMappedResults();
		return aggResults;	
		
	}	
	@GetMapping(path = "/System_Messages/Entity/{chatId}")
	public List<Message> getMessagesByEntity(@PathVariable String chatId){
		//User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		LookupOperation lookupOperation1 = LookupOperation.newLookup().from("User").localField("messageBy")
				.foreignField("_id").as("users");
		AggregationOperation match = Aggregation.match(
				Criteria.where("chatId").is(new ObjectId(chatId)));
		AggregationOperation sort = Aggregation.sort(Sort.by(Order.desc("createdAt")));
		Aggregation agg = Aggregation.newAggregation(
				match, lookupOperation1,sort);
		//Document d = mongoTemplate.aggregate(agg, "messageParticipants", MessageParticipants.class).getRawResults();
		List<Message> aggResults = mongoTemplate.aggregate(agg, "System_Messages", Message.class).getMappedResults();
		
		return aggResults;	
		
		
	}
	
	@PostMapping(path = "/System_Messages/{chatId}")
	public MessageParticipants createMessageText(@PathVariable String chatId, @RequestBody Map<String, String> data) {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		String loggedInUserId = loggerInUser.getActiveCompany().get("_id")+"";

		MessageParticipants rp = null;
		Message message = new Message();
		message.setChatId(new ObjectId(chatId));
		
		message.setCreatedAt(new Date());
		message.setText(data.get("text"));
		message.setMessageBy(new ObjectId(loggedInUserId));
		mongoTemplate.save(message, "System_Messages");

		List<ChatParticipants> cps = mongoTemplate.find(
				new Query().addCriteria(Criteria.where("chatId").is(new ObjectId(chatId)).and("status").is(true)),
				ChatParticipants.class);
		List<MessageParticipants> mps = new ArrayList<MessageParticipants>();
		for (ChatParticipants cp : cps) {
			MessageParticipants mp = new MessageParticipants();
			mp.setMessageId(message.get_id());
			mp.setUserId(cp.getUserId());
			mp.setCreatedAt(new Date());
			mp.setUpdatedAt(new Date());
			mp.setChatId(new ObjectId(chatId	));
			cp.setLastMessageID(message.get_id());
			cp.setLastMessageTime(new Date());
			cp.setLastMessageBy(new ObjectId(loggedInUserId));
			cp.setUpdatedAt(new Date());
			mongoTemplate.save(mp);
			mongoTemplate.save(cp);
			if(cp.getUserId().toString().equals(new ObjectId(loggedInUserId).toString())) {
				rp =mp;
			}
		}
		if(rp == null) {
			ChatParticipants shatParticipants = new ChatParticipants();
			shatParticipants.setUserId(new ObjectId(loggedInUserId));
			shatParticipants.setStatus(true);
			shatParticipants.setChatId(new ObjectId(chatId));
			shatParticipants.setChatType(cps.get(0).getChatType());
			//shatParticipants.setRecipientId(new ObjectId(loggerInUser.get_id()));
			
			MessageParticipants mp = new MessageParticipants();
			mp.setMessageId(message.get_id());
			mp.setUserId(new ObjectId(loggedInUserId));
			mp.setCreatedAt(new Date());
			mp.setUpdatedAt(new Date());
			mp.setChatId(new ObjectId(chatId	));
			shatParticipants.setLastMessageID(message.get_id());
			shatParticipants.setLastMessageTime(new Date());
			shatParticipants.setLastMessageBy(new ObjectId(loggedInUserId));
			shatParticipants.setUpdatedAt(new Date());
			mongoTemplate.save(mp);
			mongoTemplate.save(shatParticipants);
			rp =mp;
		}
		rp.setMessages(new ArrayList<Message>());
		rp.getMessages().add(message);
		return rp;
	
	}

}