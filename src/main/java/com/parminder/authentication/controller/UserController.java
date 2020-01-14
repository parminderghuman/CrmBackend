package com.parminder.authentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.User;

@RestController
public class UserController {
	
	@GetMapping	(path = "/me")
	public User me() {
		 User loggerInUser =   (User) RequestContextHolder.getRequestAttributes().getAttribute("user",0);
		 return loggerInUser;	
		 	}

}
