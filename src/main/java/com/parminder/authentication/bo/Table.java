package com.parminder.authentication.bo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "system_tables")
public class Table {
	
	@Id
	String _id;
	
	String name;
	
	String icon;
	
	List<Column> columns;
	
	List<String> allowedNames;
	
	List<Permissions> permissions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public List<String> getAllowedNames() {
		return allowedNames;
	}

	public void setAllowedNames(List<String> allowedNames) {
		this.allowedNames = allowedNames;
	}

	public List<Permissions> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permissions> permissions) {
		this.permissions = permissions;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}
