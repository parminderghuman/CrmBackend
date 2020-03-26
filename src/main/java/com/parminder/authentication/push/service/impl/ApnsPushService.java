package com.parminder.authentication.push.service.impl;

import javax.management.Notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.parminder.authentication.push.bo.PushNotification;
import com.parminder.authentication.push.service.PushService;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotifications;

@Service
public class ApnsPushService implements PushService {

	@Value("${apnsCertificateFile}")
	private String apnsCertificateFile;
	
	@Value("${apnsCertificatePassword}")
	private String apnsCertificatePassword;


	@Override
	public boolean send(PushNotification pushNotification) {
		boolean pushOk = false;

		PushNotificationPayload payload = PushNotificationPayload.alert(pushNotification.getTitle());
		try {
			PushedNotifications NOTIFICATIONS = Push.payload(payload, apnsCertificateFile, apnsCertificatePassword	, true,
					pushNotification.getDeviceToken());
			if (NOTIFICATIONS != null) {
				System.out.println(NOTIFICATIONS);
				pushOk = true;
			}
		} catch (CommunicationException e) {
			e.printStackTrace();
		} catch (KeystoreException e) {
			e.printStackTrace();
		}
		return pushOk;
	}

}
