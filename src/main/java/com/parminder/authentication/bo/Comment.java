package com.parminder.authentication.bo;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "System_Comments")

public class Comment {

	public enum CommenType {
		text, file
	}

	@Id
	String _id;

	ObjectId commentBy;

	String text;

	String mediaPath;

	Date createdAt;

	ObjectId entityClass;

	ObjectId entityId;

	CommenType commenType;

	@JsonIgnore
	List<User> users;

	@Transient
	User user;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public ObjectId getCommentBy() {
		return commentBy;
	}

	public void setCommentBy(ObjectId commentBy) {
		this.commentBy = commentBy;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
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

	public CommenType getCommenType() {
		return commenType;
	}

	public void setCommenType(CommenType commenType) {
		this.commenType = commenType;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User getUser() {
		if (this.users != null && this.users.size() > 0) {
			return users.get(0);
		}
		return user;
	}

	public void setUser(User user) {
		if (this.users != null && this.users.size() > 0) {
			user = users.get(0);
		}
		this.user = user;
	}

}
