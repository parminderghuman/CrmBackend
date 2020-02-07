package com.parminder.authentication.bo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.parminder.authentication.AuthenticationApplication.ObjectIdSerializer;
	
public class Column {

	public enum Type {
		Address,Boolean, Integer, Double, Date, Long, String, ObjectId, File, Reference,Select,Password, MultiSelect,MultiObject,Section
	}

	String name;

	String displayName;
			
	Type type;
	
	String condition;
	
	List<String> options;

	boolean uniqueValue;

	boolean nullValue;
	
	boolean dropDownValue;
	
	boolean participant;
	
	String defaultValue;
	
	String targetClass;
		
	boolean listValue;

	public boolean isListValue() {
		return listValue;
	}

	public void setListValue(boolean listValue) {
		this.listValue = listValue;
	}

	public boolean isDropDownValue() {
		return dropDownValue;
	}

	public void setDropDownValue(boolean dropDownValue) {
		this.dropDownValue = dropDownValue;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isUniqueValue() {
		return uniqueValue;
	}

	public void setUniqueValue(boolean uniqueValue) {
		this.uniqueValue = uniqueValue;
	}

	public boolean isNullValue() {
		return nullValue;
	}

	public void setNullValue(boolean nullValue) {
		this.nullValue = nullValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public boolean isParticipant() {
		return participant;
	}

	public void setParticipant(boolean participant) {
		this.participant = participant;
	}

	
}
