package com.parminder.authentication.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.VariableOperators.Map;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.User.Status;
import com.parminder.authentication.bo.User.UserType;
import com.parminder.authentication.repository.UserRepository;
import com.parminder.authentication.service.UserService;

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
        com.parminder.authentication.bo.User user = userRepository.findByUsername(username);
        if (user == null) {
        	
            throw new UsernameNotFoundException(username);
        }
        boolean isEnable = true;
		boolean isUserNotExpired = true;
		boolean isCredetialNotExpired = true;
		boolean isAcoountNotLocked = true;
        if(user.getStatus() !=Status.Active ) {
        //	isEnable = false;
        }
        
        Set grantedAuthorities = new HashSet<>();
		//grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" +user.getUserType()));
        RequestContextHolder.getRequestAttributes().setAttribute("user",user,0);
        Genric m = mongoTemplate.findById(user.get_id()+"-users-"+"password",Genric.class,"encoded_passwords");
        return new User(user.get_id().toString(), m.get("value").toString(), isEnable, isUserNotExpired, isCredetialNotExpired, isAcoountNotLocked, grantedAuthorities);
    }
	
}