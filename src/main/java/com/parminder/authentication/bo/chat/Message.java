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
import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.Location;
import com.parminder.authentication.bo.User;

@Document(collection = "System_Messages	")

public class Message {

	@Id
	@JsonSerialize(using = ObjectIdSerializer.class)

	ObjectId _id;
	@JsonSerialize(using = ObjectIdSerializer.class)

	ObjectId createdBy;
	@JsonSerialize(using = ObjectIdSerializer.class)

	ObjectId chatId;
	@JsonSerialize(using = ObjectIdSerializer.class)

	ObjectId messageBy;

	@JsonIgnore
	List<Genric> users;

	@Transient
	Genric user;

	String text;

	boolean system;

	String image;

	String video;
	String audio;
	Location location;

	List<ChatParticipants> participants;

	Date createdAt;

	Date updatedAt;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public ObjectId getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(ObjectId createdBy) {
		this.createdBy = createdBy;
	}

	public ObjectId getChatId() {
		return chatId;
	}

	public void setChatId(ObjectId chatId) {
		this.chatId = chatId;
	}

	public ObjectId getMessageBy() {
		return messageBy;
	}

	public void setMessageBy(ObjectId messageBy) {
		this.messageBy = messageBy;
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
		return 		this.user = this.users!= null && this.users.size() >0 ?  this.users.get(0):null	;
	
	}

	public void setUser(Genric user) {
		this.user = this.users!= null && this.users.size() >0 ?  this.users.get(0):null	;

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<ChatParticipants> getParticipants() {
		return participants;
	}

	public void setParticipants(List<ChatParticipants> participants) {
		this.participants = participants;
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

}
