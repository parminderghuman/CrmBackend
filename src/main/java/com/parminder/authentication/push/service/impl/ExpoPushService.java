package com.parminder.authentication.push.service.impl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.parminder.authentication.push.bo.PushNotification;
import com.parminder.authentication.push.service.PushService;

@Service
public class ExpoPushService implements PushService{

	@Autowired
	RestTemplate restTemplate;
	
	public Object send(HashMap<String, String> request) {
		return restTemplate.postForObject("https://exp.host/--/api/v2/push/send", request ,  Object.class);
	}
	
	public boolean send(PushNotification pushNotification) {
		HashMap<String, String>  req = new HashMap<String, String> ();
		req.put("title", pushNotification.getTitle());
		req.put("body", pushNotification.getNotification());
		req.put("to", pushNotification.getDeviceToken());
		
		Object resp = send(req);
		return true;
	}

}
