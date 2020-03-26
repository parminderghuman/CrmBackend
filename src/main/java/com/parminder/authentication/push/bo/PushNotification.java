package com.parminder.authentication.push.bo;

public class PushNotification {

	public PushNotification() {

	}

	public PushNotification(String title, String notification, String data, String deviceToken, String deviceType) {
		this.title = title;
		this.notification = notification;
		this.data = data;
		this.deviceToken = deviceToken;
		this.deviceType = deviceType;
	}

	String title;
	String notification;
	String data;
	String deviceToken;
	String deviceType;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

}
