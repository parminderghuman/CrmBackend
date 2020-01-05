package com.parminder.authentication.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.support.RequestContext;

import com.parminder.authentication.bo.Column;
import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.Table;
import com.parminder.authentication.bo.User;
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
			@PathVariable(required = false) int size) {
		List<Genric> l = mongoTemplate.find(new Query().limit(size).skip(page * size), Genric.class, entity);
		l.forEach(j -> {
			j.put("_id", j.get("_id") + "");
		});
		return l;
	}

	@GetMapping(path = "{entity}")
	public List<Genric> getEntity(@PathVariable String entity) {

		List<Genric> l = mongoTemplate.find(new Query(), Genric.class, entity);
		l.forEach(j -> {
			j.put("_id", j.get("_id") + "");
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
	public Genric CreateTable(@PathVariable String entity, @RequestBody Genric data) {

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
			} else if (column.getType() == Column.Type.Password && data.containsKey(column.getName())
					&& data.get(column.getName() + "") != null
					&& !data.get(column.getName() + "").toString().trim().isEmpty()) {
				passwordMap.put(column.getName(),
						bCryptPasswordEncoder.encode(data.get(column.getName() + "").toString()));
			}
		});
		if (data.containsKey("_id")) {
			saveMap.put("_id", new ObjectId(data.get("_id") + ""));
		}
		saveMap.put("updatedBy", user.get_id());
		saveMap.put("updateAt", new Date());
		if (data.get_id() == null) {
			// saveMap.setCreatedAt(saveMap.getUpdateAt());
			// saveMap.setCreatedBy(user.get_id());
			saveMap.put("createdAt", saveMap.getUpdateAt());
			saveMap.put("createdBy", user.get_id());
		}
		Genric l = mongoTemplate.save(saveMap, entity);
		l.put("_id", l.get("_id") + "");
		if (passwordMap != null && passwordMap.size() > 0) {
			for (String key : passwordMap.keySet()) {
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_id", l.get("_id") + entity);
				map.put("value", passwordMap.get(key));
				mongoTemplate.save(saveMap, "encoded_passwords");
				
			}
		}
		return l;
	}

}
