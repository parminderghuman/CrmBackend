
package com.parminder.authentication.bo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "users")
public class User {
	public enum UserType {
		SuperAdmin, User, Admin,Driver
	}

	public enum Status {
		Active, Inactive, Deleted
	}

	@Id		
	String _id;

	String email;
	
	String mobileNumber; 
	//@JsonIgnore
	String password;
	
	UserType userType;
	
	List<String> role;	
	
	Status status;
	
	String parent_id ;
	
	@Transient
	String id;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public List<String> getRole() {
		return role;
	}
	public void setRole(List<String> roles) {
		this.role = roles;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public UserType getUserType() {
		return userType;
	}
	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getId() {
		if(this._id != null) {
			id= _id.toString();
		}	
		return id;
	}
	public void setId(String id) {
		if(this._id != null) {
			id= _id.toString();
		}
		this.id = id;
	}
	public String getParent_id() {
		return parent_id;
	}
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}
	
		
	
}
