package com.parminder.authentication.bo;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.parminder.authentication.AuthenticationApplication.ObjectIdMapSerializer;
import com.parminder.authentication.AuthenticationApplication.ObjectIdSerializer;

@Document(collection = "System_Table_Permissions")
public class TablePermission {

	@JsonSerialize(using = ObjectIdSerializer.class)
	ObjectId _id;
	@JsonSerialize(using = ObjectIdSerializer.class)
	ObjectId parentId;
	@JsonSerialize(using = ObjectIdSerializer.class)
	ObjectId classId;
	//@JsonSerialize(using = ObjectIdMapSerializer.class)
	Map<String, Permissions> rolePermissions;
	//@JsonSerialize(using = ObjectIdMapSerializer.class)
	Map<String, Map<String, ColumnPermission>> columnPermissions;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public ObjectId getParentId() {
		return parentId;
	}

	public void setParentId(ObjectId parentId) {
		this.parentId = parentId;
	}

	public ObjectId getClassId() {
		return classId;
	}

	public void setClassId(ObjectId classId) {
		this.classId = classId;
	}

	public Map<String, Permissions> getRolePermissions() {
		return rolePermissions;
	}

	public void setRolePermissions(Map<String, Permissions> rolePermissions) {
		this.rolePermissions = rolePermissions;
	}

	public Map<String, Map<String, ColumnPermission>> getColumnPermissions() {
		return columnPermissions;
	}

	public void setColumnPermissions(Map<String, Map<String, ColumnPermission>> columnPermissions) {
		this.columnPermissions = columnPermissions;
	}

}
