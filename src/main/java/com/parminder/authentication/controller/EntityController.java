package com.parminder.authentication.controller;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.omg.CosNaming.IstringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.RequestContext;

import com.parminder.authentication.bo.Column;
import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.Permissions;
import com.parminder.authentication.bo.Role;
import com.parminder.authentication.bo.Table;
import com.parminder.authentication.bo.TablePermission;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.User.UserType;
import com.parminder.authentication.repository.TablePermissionRepository;
import com.parminder.authentication.repository.TableRepository;
import com.parminder.authentication.repository.UserRepository;

import io.jsonwebtoken.lang.Collections;

@RestController
public class EntityController {

	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	TablePermissionRepository tablePermissionRepository;
	
	@Autowired
	ChatController chatController;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired 
	UserRepository userRepository;

	@Autowired
	TableRepository tableRepository;

	@GetMapping(path = "{entity}/{page}/{size}")
	public List<Genric> getEntity(@PathVariable String entity, @PathVariable(required = false) int page,
			@PathVariable(required = false) int size, @RequestBody HashMap<String, Object> query) {
		List<Genric> l = mongoTemplate.find(new Query().limit(size).skip(page * size), Genric.class, entity);
		l.forEach(j -> {
			j.put("_id", j.get("_id") + "");
		});
		return l;
	}

	@GetMapping(path = "{entity}")
	public List<Genric> getEntity(@PathVariable String entity, @RequestParam HashMap<String, Object> searchCreteria,
			HttpServletRequest request) throws Exception {

		User user = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		List<String> roles = user.getRole();
		Query query = new Query();
		Table table = tableRepository.findByName(entity);
		Table parentClass = table;
		if (table.getAlias()) {
			parentClass = tableRepository.findById(table.getParentClass().toString()).get();
		}
		if (user.getUserType() == UserType.SuperAdmin || user.getUserType() == UserType.CompanyAdmin) {

		} else {
			TablePermission tablePermission = tablePermissionRepository.findByClassIdAndParentId(new ObjectId(table.get_id()),new ObjectId(user.getParent_id()));
			Criteria c  =new Criteria();
			for(Entry<String, Permissions> entrySet : tablePermission.getRolePermissions().entrySet()) {
				String role = entrySet.getKey();
				Permissions p = entrySet.getValue();
				
				boolean isFirst = true;
				if(user.getRole().contains(role)) {
					if(p.getReadRule() != null && !p.getReadRule().trim().isEmpty()) {
						JSONObject jsonObject = new JSONObject(p.getReadRule());
						JSONObject userJson = new JSONObject(user);
						for (String key : jsonObject.keySet()) {
							String value = jsonObject.getString(key);
							String[] val = value.split("\\.");
							if (val[0].equals("$loggedInUser")) {
								System.out.println("key "+key+" : "+userJson.get(val[1]));
								if(isFirst) {
									c.and(key).is(new ObjectId(userJson.get(val[1]) + ""));
									isFirst=false;
								}else {
									c.orOperator(Criteria.where(key).is(new ObjectId(userJson.get(val[1]) + "")));
								}
							}
						}
					}
				}
			}
			query.addCriteria(c);
			
		}
		request.getParameterMap().forEach((key, valye) -> {
			if (key.equals("parent_id")) {
				query.addCriteria(Criteria.where("parent_id").is(new ObjectId(searchCreteria.get(key) + "")));

			} else if (key.equals("_id")) {
				query.addCriteria(Criteria.where(key).is(new ObjectId(searchCreteria.get(key) + "")));
			} else {
				Object b = searchCreteria.get(key);
				try {
					JSONObject bi = new JSONObject(b.toString());
					for (String biKey : bi.keySet()) {
						if (biKey.equals("$in")) {
							query.addCriteria(Criteria.where(key).in(new ObjectId(bi.get(biKey) + "")));
						}
					}
				} catch (Exception e) {
					query.addCriteria(Criteria.where(key).is(b + ""));
				}
			}

		});

		List<Genric> l = mongoTemplate.find(query, Genric.class, entity);
		l.forEach(j -> {
			j = forMattData(table, j);
		});
		return l;
	}

	public Genric forMattData(Table table, Genric l) {

		l.put("_id", l.get("_id") + "");

		table.getColumns().forEach(actions ->

		{
			if (actions.getType() == Column.Type.ObjectId && l.containsKey(actions.getName())
					&& l.get(actions.getName()) != null) {
				l.put(actions.getName(), l.get(actions.getName()) + "");
			}
			if (actions.getType() == Column.Type.MultiObject && l.containsKey(actions.getName())
					&& l.get(actions.getName()) != null) {
				Object v = l.get(actions.getName());
				if (v instanceof Array) {
					v = Arrays.asList(v);
				}
				if (v instanceof List) {
					List<String> sp = new ArrayList<String>();
					for (Object s : (List) v) {
						sp.add(s + "");
					}
					v = sp;
				}
				l.put(actions.getName(), v);
			}
		});
		return l;
	}

	@GetMapping(path = "/{entity}/{id}")
	public Genric getTables(@PathVariable String entity, @PathVariable String id) {
		Genric l = mongoTemplate.findById(id, Genric.class, entity);
		Table table = tableRepository.findByName(entity);
		l.put("_id", l.get("_id") + "");
//		l.forEach((s, k) -> {
//			if (k instanceof ObjectId) {
//				l.put(s, k + "");
//			}
//		});
		l = forMattData(table, l);
		return l;
	}

	@PostMapping(path = "/{entity}")
	public Genric CreateTable(@PathVariable String entity, @RequestBody Genric data) throws Exception {
		final StringBuffer commentName = new StringBuffer();
		;
		List<ObjectId> chatUserList = new ArrayList<ObjectId>();
		User user = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		Table table = tableRepository.findByName(entity);
		 Genric saveMap1 = new Genric();
		if(data.containsKey("_id")) {
			saveMap1= mongoTemplate.findById(new ObjectId(data.get("_id")+""),Genric.class, entity);
		}
		final Genric saveMap = saveMap1;
		
		Map<String, Object> passwordMap = new HashMap<String, Object>();
		table.getColumns().forEach(column -> {
			if (column.getType() == Column.Type.Boolean && data.containsKey(column.getName())) {
				saveMap.put(column.getName(), Boolean.parseBoolean(data.get(column.getName()) + ""));
			} else if (column.getType() == Column.Type.Double && data.containsKey(column.getName())) {
				saveMap.put(column.getName(), Double.parseDouble(data.get(column.getName()) + ""));
			} else if (column.getType() == Column.Type.Integer && data.containsKey(column.getName())) {
				saveMap.put(column.getName(), Integer.parseInt(data.get(column.getName()) + ""));
			} else if (column.getType() == Column.Type.Long && data.containsKey(column.getName())) {
				saveMap.put(column.getName(), Long.parseLong(data.get(column.getName()) + ""));
			} else if (column.getType() == Column.Type.String && data.containsKey(column.getName())) {
				saveMap.put(column.getName(), data.get(column.getName() + ""));
			} else if (column.getType() == Column.Type.Select && data.containsKey(column.getName())) {
				saveMap.put(column.getName(), data.get(column.getName() + ""));
			} else if (column.getType() == Column.Type.Date && data.containsKey(column.getName())) {
				saveMap.put(column.getName(), data.get(column.getName() + ""));
			} else if (column.getType() == Column.Type.Address && data.containsKey(column.getName())) {
				saveMap.put(column.getName(), data.get(column.getName() + ""));
			} else if (column.getType() == Column.Type.MultiSelect && data.containsKey(column.getName())) {
				Object v = data.get(column.getName() + "");
				if (v instanceof String) {
					v = ((String) v).split(",");
				}
				saveMap.put(column.getName(), v);
			} else if (column.getType() == Column.Type.MultiObject && data.containsKey(column.getName())) {
				Object v = data.get(column.getName() + "");
				if (v instanceof String) {
					String[] k = ((String) v).split(",");
					v = Arrays.asList(k);

				}
				if (v instanceof String[]) {
					v = Arrays.asList(v);
				}
				if (v instanceof List) {
					List<ObjectId> sp = new ArrayList<ObjectId>();
					for (String s : (List<String>) v) {
						sp.add(new ObjectId(s + ""));
						if (column.isParticipant()) {
							User use = userRepository.findById(s+"").get();
							if(use != null) {
								chatUserList.add(new ObjectId(s+""));
							}
						}
					}
					v = sp;

				}
				
				saveMap.put(column.getName(), v);
			} else if (column.getType() == Column.Type.ObjectId && data.containsKey(column.getName())) {
				saveMap.put(column.getName(), new ObjectId(data.get(column.getName() + "") + ""));
				if (column.isParticipant()) {
					User use = userRepository.findById(data.get(column.getName() + "")+"" ).get();
					if(use != null) {
						chatUserList.add(new ObjectId(data.get(column.getName() + "") + ""));
					}
					
				}

			} else if (column.getType() == Column.Type.Password && data.containsKey(column.getName())
					&& data.get(column.getName() + "") != null
					&& !data.get(column.getName() + "").toString().trim().isEmpty()) {
				passwordMap.put(column.getName(),
						bCryptPasswordEncoder.encode(data.get(column.getName() + "").toString()));
			}
			if (column.isDropDownValue()) {
				commentName.append(data.get(column.getName() + "").toString());
			}
		});
		if (table.getParentClass() != null && data.get("parent_id") == null) {
			throw new Exception("Parent not found");
		} else if (table.getParentClass() != null) {
			Table pTable = tableRepository.findById(table.getParentClass().toString()).get();
			Genric ob = mongoTemplate.findById(new ObjectId(data.get("parent_id") + ""), Genric.class,
					pTable.getName());
			if (ob == null) {
				throw new Exception("Parent not found");
			} else {
				saveMap.put("parent_id", new ObjectId(data.get("parent_id") + ""));
			}
		}
		try {
			if (data.containsKey("_id")) {
				saveMap.put("_id", new ObjectId(data.get("_id").toString()));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		saveMap.put("updatedBy", user.get_id());
		saveMap.put("updateAt", new Date());
		if(data.containsKey("userType") ) {
			saveMap.put("userType", data.get("userType"));
		}
		if (!data.containsKey("_id")) {
			saveMap.put("createdAt", saveMap.get("updateAt"));
			saveMap.put("createdBy", user.get_id());

		} else {
			saveMap.put("createdAt", data.get("updateAt"));
			if (data.get("createdBy") != null) {
				try {
					saveMap.put("createdBy", new ObjectId(data.get("createdBy") + ""));
				} catch (Exception e) {

				}

			}
			if (data.get("createdAt") != null) {
				try {
					saveMap.put("createdAt", data.get("createdAt"));
				} catch (Exception e) {

				}

			}
		}
		Genric l = mongoTemplate.save(saveMap, entity);
		chatUserList.add(new ObjectId(user.get_id()));
		chatController.createEntityChat(commentName.toString(), new ObjectId(table.get_id()),
				new ObjectId(l.get("_id") + ""), chatUserList);
		l.put("_id", l.get("_id") + "");
		if (passwordMap != null && passwordMap.size() > 0) {
			for (String key : passwordMap.keySet()) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_id", l.get("_id") + "-" + entity + "-" + key);
				map.put("value", passwordMap.get(key));
				mongoTemplate.save(map, "encoded_passwords");

			}
		}
		return l;
	}

}
