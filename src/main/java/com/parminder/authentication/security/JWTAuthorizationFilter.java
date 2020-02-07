package com.parminder.authentication.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.User.Status;
import com.parminder.authentication.config.JwtConfig;
import com.parminder.authentication.repository.UserRepository;
import com.parminder.authentication.service.UserService;

import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	private final JwtConfig jwtConfig;
	UserRepository userRepository;
	MongoTemplate mongoTemplate;

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig,
			UserRepository userRepository,MongoTemplate mongoTemplate) {
		super(authenticationManager);
		this.jwtConfig = jwtConfig;
		this.userRepository = userRepository;
		this.mongoTemplate =mongoTemplate;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		String header = req.getHeader(jwtConfig.getHeader());
		if ((header == null || header.isEmpty()) && (req.getParameter(jwtConfig.getHeader()) == null
				|| req.getParameter(jwtConfig.getHeader()).isEmpty())) {
			chain.doFilter(req, res);
			return;
		}
		UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(jwtConfig.getHeader());
		if (token == null || token.isEmpty()) {
			token = request.getParameter(jwtConfig.getHeader());
		}
		if (token != null) {
			// parse the token.
			token = token.substring(jwtConfig.getPrefix().length());
			String email = Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes()).parseClaimsJws(token).getBody()
					.getSubject();
			if (email != null) {

				User user = userRepository.findById(email).get();
					Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
				 boolean isEnable = true;
					boolean isUserNotExpired = true;
					boolean isCredetialNotExpired = true;
					boolean isAcoountNotLocked = true;
			        if(user.getStatus() !=Status.Active ) {
			        	isEnable = false;
			        }
			        
			      
					grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" +user.getUserType()));
			        RequestContextHolder.getRequestAttributes().setAttribute("user",user,0);
			        //return new User(user.getEmail(), user.getPassword(), isEnable, isUserNotExpired, isCredetialNotExpired, isAcoountNotLocked, grantedAuthorities);
			        Genric m = mongoTemplate.findById(user.get_id()+"-users-"+"password",Genric.class,"encoded_passwords");
			      //  mongoTemplate.find(new Query().addCriteria(Criteria.where(key)), entityClass)
				return new UsernamePasswordAuthenticationToken(user.getUsername(), m.get("value").toString(),	grantedAuthorities);

			}
		}
		return null;
	}

}
