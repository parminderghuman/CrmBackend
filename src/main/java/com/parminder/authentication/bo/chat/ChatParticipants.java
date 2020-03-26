package com.parminder.authentication.bo.chat;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.parminder.authentication.AuthenticationApplication.ObjectIdSerializer;
import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.User;
@Document(collection = "System_Chat_Particpants")


public class ChatParticipants {

	public enum ChatType {
		OneToOne, Group, Entity
	}

	@Id
	@JsonSerialize(using=ObjectIdSerializer.class)

	ObjectId id;
	@JsonSerialize(using=ObjectIdSerializer.class)

	ObjectId userId;
	@JsonSerialize(using=ObjectIdSerializer.class)
	
	ObjectId systemUserId;

	ObjectId chatId;
	@JsonSerialize(using=ObjectIdSerializer.class)

	ObjectId lastMessageID;
	@JsonSerialize(using=ObjectIdSerializer.class)

	ObjectId recipientId;
	@JsonSerialize(using=ObjectIdSerializer.class)

	ObjectId lastMessageBy;
	
	@LastModifiedDate	
	Date updatedAt;

	@CreatedDate
	Date createdAt;
	
	Date lastMessageTime;
	
	ChatType chatType;
	
	@JsonIgnore
	List<Genric> users;

	@Transient
	Genric user;
	
	

	@JsonIgnore
	List<Genric> entities;

	@Transient
	Genric entity;
	
	@JsonIgnore
	List<Chat> chats;

	@Transient
	Chat chat;
	@JsonIgnore
	List<Message> messages;

	@Transient
	Message message;
	
	public ChatType getChatType() {
		return chatType;
	}

	public void setChatType(ChatType chatType) {
		this.chatType = chatType;
	}

	boolean status;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public ObjectId getChatId() {
		return chatId;
	}

	public void setChatId(ObjectId chatId) {
		this.chatId = chatId;
	}

	public ObjectId getLastMessageID() {
		return lastMessageID;
	}

	public void setLastMessageID(ObjectId lastMessageID) {
		this.lastMessageID = lastMessageID;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(ObjectId recipientId) {
		this.recipientId = recipientId;
	}	
	public Genric getUser() {
		if (this.users != null && this.users.size() > 0) {
			return users.get(0);
		}
		return user;
	}

	public void setUser(Genric user) {
		if (this.users != null && this.users.size() > 0) {
			user = users.get(0);
		}
		this.user = user;
	}

	public List<Genric> getUsers() {
		return users;
	}

	public void setUsers(List<Genric> users) {
		this.users = users;
	}

	public Date getLastMessageTime() {
		return lastMessageTime;
	}

	public void setLastMessageTime(Date lastMessageTime) {
		this.lastMessageTime = lastMessageTime;
	}

	public List<Chat> getChats() {
		return chats;
	}

	public void setChats(List<Chat> chats) {
		this.chats = chats;
	}

	public Chat getChat() {
		if (this.chats != null && this.chats.size() > 0) {
		return chats.get(0);
	}
		return chat;
	}

	public void setChat(Chat chat) {if (this.chats != null && this.chats.size() > 0) {
		chat	 = chats.get(0);
	}
		this.chat = chat;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public Message getMessage() {
		return this.message = this.messages!= null && this.messages.size() >0 ?  this.messages.get(0):null;

	}

	public void setMessage(Message message) {
				this.message = this.messages!= null && this.messages.size() >0 ?  this.messages.get(0):null	;

	}

	public ObjectId getLastMessageBy() {
		return lastMessageBy;
	}

	public void setLastMessageBy(ObjectId lastMessageBy) {
		this.lastMessageBy = lastMessageBy;
	}

	public List<Genric> getEntities() {
		return entities;
	}

	public void setEntities(List<Genric> entities) {
		this.entities = entities;
	}

	public Genric getEntity() {
		return 	this.entity = this.entities!= null && this.entities.size() >0 ?  this.entities.get(0):null	;

	}

	public void setEntity(Genric entity) {
		this.entity = this.entities!= null && this.entities.size() >0 ?  this.entities.get(0):null	;

	}

	public ObjectId getSystemUserId() {
		return systemUserId;
	}

	public void setSystemUserId(ObjectId systemUserId) {
		this.systemUserId = systemUserId;
	}
	
}
