package com.parminder.authentication.bo;

public class Permissions {

	String role;

	boolean read;

	boolean write;

	boolean delete;

	String readRule; 
	
	String rule;
	
	boolean canAdd;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isWrite() {
		return write;
	}

	public void setWrite(boolean write) {
		this.write = write;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public boolean isCanAdd() {
		return canAdd;
	}

	public void setCanAdd(boolean canAdd) {
		this.canAdd = canAdd;
	}

	public String getReadRule() {
		return readRule;
	}

	public void setReadRule(String readRule) {
		this.readRule = readRule;
	}

}
