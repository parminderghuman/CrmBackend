package com.parminder.authentication.controller;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.support.RequestContext;

import com.parminder.authentication.bo.Column;
import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.Permissions;
import com.parminder.authentication.bo.Role;
import com.parminder.authentication.bo.Table;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.User.UserType;
import com.parminder.authentication.repository.TableRepository;

@RestController
public class EntityController {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

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
		if (user.getUserType() == UserType.SuperAdmin) {

		} else {

			Table table = tableRepository.findByName(entity);
			Table parentClass = table;
			if (table.getAlias()) {
				parentClass = tableRepository.findById(table.getParentClass()).get();
			}
			Permissions p = null;
			for (Permissions action : parentClass.getPermissions()) {
				if ("*".equals(action.getRole())) {
					p = action;
				} else if (roles.contains(action.getRole())) {
					p = action;
				}

			}
			if ("*".equals(p.getRule())) {

			} else if (p.isRead()) {
				if (p.getReadRule() != null) {
					Genric genricUser = mongoTemplate.findById(user.get_id(), Genric.class, "users");
					JSONObject jsonObject = new JSONObject(p.getReadRule());
					for (String key : jsonObject.keySet()) {
						String value = jsonObject.getString(key);
						String[] val = value.split("\\.");
						if (val[0].equals("$loggedInUser")) {
							query.addCriteria(Criteria.where(key).is(new ObjectId(genricUser.get(val[1]) + "")));
						}
					}

				}
			} else {
				throw new Exception("dont have permission");
			}
		}

		request.getParameterMap().forEach((key, valye) -> {
			if (key.equals("parent_id")) {
				query.addCriteria(Criteria.where("parent_id").is(new ObjectId(searchCreteria.get("parent_id") + "")));
			}
		});

		List<Genric> l = mongoTemplate.find(query, Genric.class, entity);
		l.forEach(j -> {
			j.put("_id", j.get("_id") + "");
			j.forEach((s, k) -> {
				if (k instanceof ObjectId) {
					j.put(s, k + "");
				}
			});
		});
		return l;
	}

	@GetMapping(path = "/{entity}/{id}")
	public Genric getTables(@PathVariable String entity, @PathVariable String id) {
		Genric l = mongoTemplate.findById(id, Genric.class, entity);

		l.put("_id", l.get("_id") + "");

		return l;
	}

	@PostMapping(path = "/{entity}")
	public Genric CreateTable(@PathVariable String entity, @RequestBody Genric data) throws Exception {

		User user = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		Table table = tableRepository.findByName(entity);
		Genric saveMap = new Genric();
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
			} else if (column.getType() == Column.Type.MultiSelect && data.containsKey(column.getName())) {
				Object v = data.get(column.getName() + "");
				if (v instanceof String) {
					v = ((String) v).split(",");
				}
				saveMap.put(column.getName(), v);
			} else if (column.getType() == Column.Type.MultiObject && data.containsKey(column.getName())) {
				Object v = data.get(column.getName() + "");
				if (v instanceof String) {
					v = ((String) v).split(",");
				}

				saveMap.put(column.getName(), v);
			} else if (column.getType() == Column.Type.ObjectId && data.containsKey(column.getName())) {
				saveMap.put(column.getName(), new ObjectId(data.get(column.getName() + "") + ""));
			} else if (column.getType() == Column.Type.Password && data.containsKey(column.getName())
					&& data.get(column.getName() + "") != null
					&& !data.get(column.getName() + "").toString().trim().isEmpty()) {
				passwordMap.put(column.getName(),
						bCryptPasswordEncoder.encode(data.get(column.getName() + "").toString()));
			}
		});
		if (table.getParentClass() != null && data.get("parent_id") == null) {
			throw new Exception("Parent not found");
		} else if (table.getParentClass() != null) {
			Table pTable = tableRepository.findById(table.getParentClass()).get();
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
		if (!data.containsKey("_id")) {
			saveMap.put("createdAt", saveMap.get("updateAt"));
			saveMap.put("createdBy", user.get_id());
		} else {
			saveMap.put("createdAt", data.get("updateAt"));
			if(data.get("createdBy") != null) {
				try {
				saveMap.put("createdBy", new ObjectId(data.get("createdBy") + ""));
				}catch(Exception e) {
					
				}

			}
		}
		Genric l = mongoTemplate.save(saveMap, entity);
		l.put("_id", l.get("_id") + "");
		if (passwordMap != null && passwordMap.size() > 0) {
			for (String key : passwordMap.keySet()) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_id", l.get("_id") + entity);
				map.put("value", passwordMap.get(key));
				mongoTemplate.save(map , "encoded_passwords");

			}
		}
		return l;
	}

}
