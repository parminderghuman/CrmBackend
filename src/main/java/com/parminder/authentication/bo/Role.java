package com.parminder.authentication.bo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.parminder.authentication.AuthenticationApplication.ObjectIdSerializer;

@Document(collection = "role")
public class Role {
	
	@Id
	String _id;
	
	String name;

	

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
