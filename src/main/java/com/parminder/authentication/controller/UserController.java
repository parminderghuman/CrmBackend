package com.parminder.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.User;
import com.parminder.authentication.repository.UserRepository;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;

	@GetMapping(path = "/me")
	public User me() {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		return loggerInUser;
	}

	@GetMapping(path = "/users/{id}")
	public User UserDetail(@PathVariable String id) {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		return userRepository.findById(id).get();
	}

}
