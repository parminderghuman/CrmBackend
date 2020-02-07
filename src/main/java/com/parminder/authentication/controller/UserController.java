package com.parminder.authentication.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.repository.UserRepository;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;
	@Autowired
	MongoTemplate	mongoTemplate;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

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
	@PostMapping(path = "/users/reset-password")
	public boolean UserDetail(@RequestBody Map<String,String> map) {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		
		String oldPassword = map.get("oldPassword");
		String rePassword = map.get("rePassword");
		String password = map.get("password");
		if(!rePassword.equals(password)) {
			return false;
		}
		
        Genric m = mongoTemplate.findById(loggerInUser.get_id()+"-users-"+"password",Genric.class,"encoded_passwords");
        if(m != null && bCryptPasswordEncoder.matches(oldPassword	,m.get("value").toString() )) {
        		m.put("value", bCryptPasswordEncoder.encode(password));
        		mongoTemplate.save(m,"encoded_passwords");
//        		Map<String, Object> map = new HashMap<String, Object>();
//				map.put("_id", l.get("_id") + "-" + entity + "-" + key);
//				map.put("value", passwordMap.get(key));
//				mongoTemplate.save(map, "encoded_passwords");
        		return true;
        }		
		return false;
	}

}
