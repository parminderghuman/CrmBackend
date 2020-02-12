package com.parminder.authentication.bo.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.parminder.authentication.AuthenticationApplication.ObjectIdSerializer;
import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.User;

public class MessageParticipants {

	
	@Id
	@JsonSerialize(using=ObjectIdSerializer.class)
	ObjectId id;
	
	@JsonSerialize(using=ObjectIdSerializer.class)
	ObjectId messageId;
	@JsonSerialize(using=ObjectIdSerializer.class)

	ObjectId userId;
	@JsonSerialize(using=ObjectIdSerializer.class)

	ObjectId chatId;
	
	boolean delivered;
	
	boolean read;
	
	Date createdAt;
	
	Date updatedAt;
	
	@JsonIgnore
	List<Genric> users;

	@Transient
	Genric user;
	
	@JsonIgnore
	List<Message> messages;

	@Transient
	Message message;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public ObjectId getMessageId() {
		return messageId;
	}

	public void setMessageId(ObjectId messageId) {
		this.messageId = messageId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public List<Genric> getUsers() {
		return users;
	}

	public void setUsers(List<Genric> users) {
		this.users = users;
	}

	public Genric getUser() {
		for(Genric g : this.users!= null ? this.users : new ArrayList<Genric>()) {
			g.put("_id", g.get("_id")+"");
		}
		return user =this.users!= null && this.users.size() >0 ?  this.users.get(0):null	;	
	}

	public void setUser(Genric user) {
		this.user = this.users!= null && this.users.size() >0 ?  this.users.get(0):null	;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public Message getMessage() {
		return message =this.messages!= null && this.messages.size() >0 ?  this.messages.get(0):null	;
	}

	public void setMessage(Message message) {
		this.message = this.messages!= null && this.messages.size() >0 ?  this.messages.get(0):null	;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public ObjectId getChatId() {
		return chatId;
	}

	public void setChatId(ObjectId chatId) {
		this.chatId = chatId;
	}
	
}
