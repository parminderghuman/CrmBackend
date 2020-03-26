package com.parminder.authentication.push.service.impl;

import org.springframework.stereotype.Service;

import com.parminder.authentication.push.bo.PushNotification;
import com.parminder.authentication.push.service.PushService;

@Service
public class FcmPushService implements PushService{

	@Override
	public boolean send(PushNotification pushNotification) {
		// TODO Auto-generated method stub
		return false;
	}

}
