package com.parminder.authentication.push.service;

import com.parminder.authentication.push.bo.PushNotification;

public interface PushService {
	public boolean send(PushNotification pushNotification);

}
