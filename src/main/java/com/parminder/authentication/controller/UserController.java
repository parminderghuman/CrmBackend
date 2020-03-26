package com.parminder.authentication.controller;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.Comment;
import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.Location;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.repository.UserRepository;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;
	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping(path = "/me")
	public User me() {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		List<Genric> companyUsers = mongoTemplate
				.find(new Query().addCriteria(Criteria.where("User").is(loggerInUser.get_id())), Genric.class, "User");
		for (Genric g : companyUsers) {
			g.put("_id", g.get("_id") + "");
			g.put("parent_id", g.get("parent_id") + "");
		}
		loggerInUser.setCompanyUsers(companyUsers);
		return loggerInUser;
	}

	@GetMapping(path = "/users/{id}")
	public User UserDetail(@PathVariable String id) {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		return userRepository.findById(id).get();
	}

	@PostMapping(path = "/users/reset-password")
	public boolean UserDetail(@RequestBody Map<String, String> map) {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);

		String oldPassword = map.get("oldPassword");
		String rePassword = map.get("rePassword");
		String password = map.get("password");
		if (!rePassword.equals(password)) {
			return false;
		}

		Genric m = mongoTemplate.findById(loggerInUser.get_id() + "-users-" + "password", Genric.class,
				"encoded_passwords");
		if (m != null && bCryptPasswordEncoder.matches(oldPassword, m.get("value").toString())) {
			m.put("value", bCryptPasswordEncoder.encode(password));
			mongoTemplate.save(m, "encoded_passwords");
//        		Map<String, Object> map = new HashMap<String, Object>();
//				map.put("_id", l.get("_id") + "-" + entity + "-" + key);
//				map.put("value", passwordMap.get(key));
//				mongoTemplate.save(map, "encoded_passwords");
			return true;
		}
		return false;
	}

	@PostMapping("/users/addLocation")
	public boolean addLocation(@RequestBody Map map) {
		if (map.containsKey("lat") && map.containsKey("lng")) {
			Double lat = Double.parseDouble(map.get("lat") + "");
			Double lng = Double.parseDouble(map.get("lng") + "");
			User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
			loggerInUser.setLocation(new Location(lat, lng));
			userRepository.save(loggerInUser);
			return true;
		}

		return false;
	}

	@GetMapping("/users/fetchLocation")
	public Document fetchLocation() {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		String parentId = loggerInUser.getActiveCompany().get("parent_id") + "";

		LookupOperation lookupOperation = LookupOperation.newLookup().from("users").localField("User")
				.foreignField("_id").as("users");
		AggregationOperation match = Aggregation.match(Criteria.where("parent_id").is(new ObjectId(parentId)));
		AggregationOperation sort = Aggregation.sort(Sort.by(Order.desc("createdAt")));
		Aggregation agg = Aggregation.newAggregation(

				match, lookupOperation, sort);
		Document d = mongoTemplate.aggregate(agg, "User", Genric.class).getRawResults();
		return d;

	}

	@PostMapping("/users/addToken")
	public boolean addDeviceToken(String deviceToken, String deviceType) {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		loggerInUser.setDeviceToken(deviceToken);
		loggerInUser.setDeviceType(deviceType);
		userRepository.save(loggerInUser);
		return true;
	}
}
