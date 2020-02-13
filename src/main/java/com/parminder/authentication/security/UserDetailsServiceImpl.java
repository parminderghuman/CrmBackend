package com.parminder.authentication.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.User.Status;
import com.parminder.authentication.repository.UserRepository;

@Service // It has to be annotated with @Service.
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	UserRepository userRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		com.parminder.authentication.bo.User user = mongoTemplate.findOne(
				new Query().addCriteria(Criteria.where("username").is(username)),
				com.parminder.authentication.bo.User.class);
		if (user == null) {

			throw new UsernameNotFoundException(username);
		}
		boolean isEnable = true;
		boolean isUserNotExpired = true;
		boolean isCredetialNotExpired = true;
		boolean isAcoountNotLocked = true;
		if (user.getStatus() != Status.Active) {
			// isEnable = false;
		}

		Set grantedAuthorities = new HashSet<>();
		// grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"
		// +user.getUserType()));
		RequestContextHolder.getRequestAttributes().setAttribute("user", user, 0);
		Genric m = mongoTemplate.findById(user.get_id() + "-users-" + "password", Genric.class, "encoded_passwords");
		List<Genric> g = mongoTemplate.find(new Query().addCriteria(Criteria.where("User").is(new ObjectId(user.get_id()))),
				Genric.class, "User");
		if(g!= null && g.size()>0) {

			return new User(user.get_id().toString()+","+g.get(0).get("_id"), m.get("value").toString(), isEnable, isUserNotExpired,
					isCredetialNotExpired, isAcoountNotLocked, grantedAuthorities);
		}

		return new User(user.get_id().toString(), m.get("value").toString(), isEnable, isUserNotExpired,
				isCredetialNotExpired, isAcoountNotLocked, grantedAuthorities);
	}

}