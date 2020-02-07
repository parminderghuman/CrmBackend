package com.parminder.authentication.bo.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.parminder.authentication.AuthenticationApplication.ObjectIdSerializer;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.chat.ChatParticipants.ChatType;
@Document(collection = "System_Chats")

public class Chat {

	@Id
	@JsonSerialize(using=ObjectIdSerializer.class)
	ObjectId id;
	

	@JsonSerialize(using=ObjectIdSerializer.class)

	ObjectId entityClass;
	@JsonSerialize(using=ObjectIdSerializer.class)

	ObjectId entityId;
	
	Date createdAt;
	
	Date updatedAt;
	@JsonSerialize(using=ObjectIdSerializer.class)

	ObjectId createdBy;
	
	ChatType type; 
	
	String name;
	
	

	
	@Transient
	List<ChatParticipants> participants;
	
	@JsonIgnore
	List<ChatParticipants> tempParticipants;

	@JsonIgnore
	List<User> tempUsers;

	@Transient
	List<User> users;
	
	public ChatType getType() {
		return type;
	}

	public void setType(ChatType type) {
		this.type = type;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	

	public ObjectId getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(ObjectId entityClass) {
		this.entityClass = entityClass;
	}

	public ObjectId getEntityId() {
		return entityId;
	}

	public void setEntityId(ObjectId entityId) {
		this.entityId = entityId;
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

	public ObjectId getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(ObjectId createdBy) {
		this.createdBy = createdBy;
	}

	public void addChatParticipants(ChatParticipants chatParticipants) {
		if(this.participants == null) {
			this.participants = new ArrayList<ChatParticipants>();
		}
		this.participants.add(chatParticipants);
	}

	public List<ChatParticipants> getTempParticipants() {
		return tempParticipants;
	}

	public void setTempParticipants(List<ChatParticipants> tempParticipants) {
		this.tempParticipants = tempParticipants;
	}
	
	public List<ChatParticipants> getParticipants() {
		return participants =  this.tempParticipants ;
	}

	public void setParticipants(List<ChatParticipants> participants) {
		participants = tempParticipants;
		this.participants = participants;
	}
	

	public List<User> getTempUsers() {
		return tempUsers;
	}

	public void setTempUsers(List<User> tempUsers) {
		this.tempUsers = tempUsers;
	}

	

	public List<User>  getUsers() {
		
		return users = tempUsers;
	}

	public void setUsers(List<User>  user) {
	
		this.users = tempUsers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
}
