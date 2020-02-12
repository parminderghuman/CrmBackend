package com.parminder.authentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomePage {
	
	
	@GetMapping(path = "/")
	public String getHomePage() {
		return "V0.1";
	}

}
