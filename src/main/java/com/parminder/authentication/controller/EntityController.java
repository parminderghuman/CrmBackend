package com.parminder.authentication.controller;

import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import com.parminder.authentication.bo.Column;
import com.parminder.authentication.bo.Genric;
import com.parminder.authentication.bo.Permissions;
import com.parminder.authentication.bo.Table;
import com.parminder.authentication.bo.TablePermission;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.User.UserType;
import com.parminder.authentication.repository.TablePermissionRepository;
import com.parminder.authentication.repository.TableRepository;
import com.parminder.authentication.repository.UserRepository;

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
		List<String> roles = new ArrayList<String>();
		if(user.getActiveCompany() != null) {
			 for(ObjectId role :  (List<ObjectId>) user.getActiveCompany().get("Role")) {
				 roles.add(role.toString());
			 }
		}
		Query query = new Query();
		Table table = tableRepository.findByName(entity);
		Table parentClass = table;
		Criteria c = new Criteria();
		if (table.getAlias()) {
			parentClass = tableRepository.findById(table.getParentClass().toString()).get();
		}
		if (user.getUserType() == UserType.SuperAdmin || "CompanyAdmin".equals(user.getActiveCompany().get("userType"))) {

		} else {
			TablePermission tablePermission = tablePermissionRepository
					.findByClassIdAndParentId(new ObjectId(table.get_id()), new ObjectId(user.getActiveCompany().get("parent_id")+""));
			for (Entry<String, Permissions> entrySet : tablePermission.getRolePermissions().entrySet()) {
				String role = entrySet.getKey();
				Permissions p = entrySet.getValue();

				boolean isFirst = true;
				if (roles.contains(role)) {
					if (p.getReadRule() != null && !p.getReadRule().trim().isEmpty()) {
						JSONObject jsonObject = new JSONObject(p.getReadRule());
						JSONObject userJson = new JSONObject(user);
						for (String key : jsonObject.keySet()) {
							String value = jsonObject.getString(key);
							String[] val = value.split("\\.");
							if (val[0].equals("$loggedInUser")) {
								System.out.println("key " + key + " : " + userJson.get(val[1]));
								JSONObject bj = userJson;
								Object queryParam = null;
								for(int ij = 1; ij< val.length;ij++ ) {
									if(ij < val.length-1 ) {
										bj = bj.getJSONObject(val[ij]);
									}else {
										queryParam = bj.get(val[ij]);
									}
								}
								if (isFirst) {
									c.and(key).is(new ObjectId(queryParam+""));
									isFirst = false;
								} else {
									c.orOperator(Criteria.where(key).is(new ObjectId(queryParam+"")));
								}
							}
						}
					}
				}
			}
			

		}
//		request.getParameterMap().forEach((key, valye) -> {
//			if (key.equals("parent_id")) {
//				query.addCriteria(Criteria.where("parent_id").is(new ObjectId(searchCreteria.get(key) + "")));
//
//			} else if (key.equals("_id")) {
//				query.addCriteria(Criteria.where(key).is(new ObjectId(searchCreteria.get(key) + "")));
//			} else {
//				Object b = searchCreteria.get(key);
//				try {
//					JSONObject bi = new JSONObject(b.toString());
//					for (String biKey : bi.keySet()) {
//						if (biKey.equals("$in")) {
//							query.addCriteria(Criteria.where(key).in(new ObjectId(bi.get(biKey) + "")));
//						}
//					}
//				} catch (Exception e) {
//					query.addCriteria(Criteria.where(key).is(b + ""));
//				}
//			}
//
//		});

		List<Order> orderList = new ArrayList<Sort.Order>();
		if (searchCreteria.containsKey("sort")) {
			try {
				JSONObject sort = new JSONObject(URLDecoder.decode(searchCreteria.get("sort").toString()));
				for (String so : sort.keySet()) {
					if ("asc".equals(sort.getString(so))) {
						orderList.add(Order.asc(so));
					} else {
						orderList.add(Order.desc(so));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//Criteria criteria = new Criteria();
		query.with(Sort.by(orderList));
		if (searchCreteria.containsKey("query")) {
			try {
				JSONObject sort = new JSONObject(URLDecoder.decode(searchCreteria.get("query").toString()));
				for (String so : sort.keySet()) {
					Object val = sort.get(so);
					if(val instanceof JSONObject) {
						JSONObject b  = new JSONObject(val.toString());
						for(String h : b.keySet()) {
							if(h.equals("$oid")){
								c.and(so ).is(new ObjectId(b.getString(h)));
							}else if(h.equals("$gt")){
								c.and(so ).gt(new Date(b.getLong(h)));
							}else if(h.equals("$lt")){
								c.and(so ).lt(new Date(b.getLong(h)));
							}
						}
					}else {
						c.and(so ).is(val);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		query.addCriteria(c);
		
		query.with(new PageRequest(0, 100));
		System.out.println(entity+" : "+query);
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
			}else if (actions.getType() == Column.Type.MultiObject && l.containsKey(actions.getName())
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
			}else if(Column.Type.Link  == actions.getType() && l.containsKey(actions.getName())) {
				Table lTable = tableRepository.findById(actions.getTargetClass()).get();
				Genric g = getTables(lTable.getName(), l.get(actions.getName()).toString());
				l.put(actions.getName(), g);
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
	public HashMap CreateTable(@PathVariable String entity, @RequestBody HashMap<String, Object> data) throws Exception {
		final StringBuffer commentName = new StringBuffer();
		;
		
		 final List<String> didChanges = new ArrayList<String>();
		List<Genric> chatUserList = new ArrayList<Genric>();
		User user = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		Table table = tableRepository.findByName(entity);
		Genric saveMap1 = new Genric();
		if (data.containsKey("_id")) {
			saveMap1 = mongoTemplate.findById(new ObjectId(data.get("_id") + ""), Genric.class, entity);
		}
		final Genric saveMap = saveMap1;

		Map<String, Object> passwordMap = new HashMap<String, Object>();
		table.getColumns().forEach(column -> {
			if (column.getType() == Column.Type.Boolean && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  Boolean.parseBoolean(saveMap.get(column.getName())+"") != Boolean.parseBoolean(data.get(column.getName()) + "")) {
					didChanges.add(column.getName());
				}
				saveMap.put(column.getName(), Boolean.parseBoolean(data.get(column.getName()) + ""));
			} else if (column.getType() == Column.Type.Double && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  Double.parseDouble(saveMap.get(column.getName())+"") != Double.parseDouble(data.get(column.getName()) + "")) {
					didChanges.add(column.getName());
				}
				saveMap.put(column.getName(), Double.parseDouble(data.get(column.getName()) + ""));
			} else if (column.getType() == Column.Type.Integer && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  Integer.parseInt(saveMap.get(column.getName())+"") != Integer.parseInt	(data.get(column.getName()) + "")) {
					didChanges.add(column.getName());
				}
				saveMap.put(column.getName(), Integer.parseInt(data.get(column.getName()) + ""));
			} else if (column.getType() == Column.Type.Long && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  Long.parseLong(saveMap.get(column.getName())+"") != Long.parseLong(data.get(column.getName()) + "")) {
					didChanges.add(column.getName());
				}
				saveMap.put(column.getName(), Long.parseLong(data.get(column.getName()) + ""));
			} else if (column.getType() == Column.Type.String && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  saveMap.get(column.getName())+"" != data.get(column.getName()) + "") {
					didChanges.add(column.getName());
				}
				saveMap.put(column.getName(), data.get(column.getName() + ""));
			} else if (column.getType() == Column.Type.Select && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  saveMap.get(column.getName())+"" != data.get(column.getName()) + "") {
					didChanges.add(column.getName());
				}
				saveMap.put(column.getName(), data.get(column.getName() + ""));
			} else if (column.getType() == Column.Type.Date && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  saveMap.get(column.getName())+"" != data.get(column.getName()) + "") {
					didChanges.add(column.getName());
				}
				saveMap.put(column.getName(), data.get(column.getName() + ""));
			} else if (column.getType() == Column.Type.Address && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  saveMap.get(column.getName())+"" != data.get(column.getName()) + "") {
					didChanges.add(column.getName());
				}
				saveMap.put(column.getName(), data.get(column.getName() + ""));
			} else if (column.getType() == Column.Type.MultiSelect && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  saveMap.get(column.getName())+"" != data.get(column.getName()) + "") {
					didChanges.add(column.getName());
				}
				Object v = data.get(column.getName() + "");
				if (v instanceof String) {
					v = ((String) v).split(",");
				}
				saveMap.put(column.getName(), v);
			} else if (column.getType() == Column.Type.MultiObject && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  saveMap.get(column.getName())+"" != data.get(column.getName()) + "") {
					didChanges.add(column.getName());
				}
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
								Genric use = mongoTemplate.findById(s  + "",Genric.class,"User");
							if (use != null) {
								chatUserList.add(use);
							}
						}
					}
					v = sp;

				}

				saveMap.put(column.getName(), v);
			} else if (column.getType() == Column.Type.ObjectId && data.containsKey(column.getName())) {
				if(!saveMap.containsKey(column.getName()) ||  saveMap.get(column.getName())+"" != data.get(column.getName()) + "") {
					didChanges.add(column.getName());
				}
				saveMap.put(column.getName(), new ObjectId(data.get(column.getName() + "") + ""));
				if (column.isParticipant()) {
					Genric use = mongoTemplate.findById(data.get(column.getName() + "") + "",Genric.class,"User");
					if (use != null) {
						chatUserList.add(use);
					}

				}

			} else if (column.getType() == Column.Type.Password && data.containsKey(column.getName())
					&& data.get(column.getName() + "") != null
					&& !data.get(column.getName() + "").toString().trim().isEmpty()) {
				passwordMap.put(column.getName(),
						bCryptPasswordEncoder.encode(data.get(column.getName() + "").toString()));
			}else if (column.getType() == Column.Type.Link && data.containsKey(column.getName())){
				Table lTable = tableRepository.findById(column.getTargetClass()).get();
				
				HashMap planned;
					try {
						planned = CreateTable(lTable.getName(),(HashMap)data.get(column.getName()));
						saveMap.put(column.getName(),new ObjectId(planned.get("_id")+""));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//throw new Exception();
					}
					
				

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
		if (data.containsKey("userType")) {
			saveMap.put("userType", data.get("userType"));
		}
		if (!data.containsKey("_id")) {
			saveMap.put("createdAt", saveMap.get("updateAt"));
			saveMap.put("createdBy", user.get_id());
			didChanges.clear();
			didChanges.add(" Add New "+entity);

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
		if(user.getActiveCompany() != null) {
			chatUserList.add(user.getActiveCompany());

		}
		
		chatController.createEntityChat(commentName.toString(), new ObjectId(table.get_id()),
				new ObjectId(l.get("_id") + ""), chatUserList,String.join(",",didChanges));
		
		l.put("_id", l.get("_id") + "");
		if (passwordMap != null && passwordMap.size() > 0) {
			for (String key : passwordMap.keySet()) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("_id", l.get("_id") + "-" + entity + "-" + key);
				map.put("value", passwordMap.get(key));
				mongoTemplate.save(map, "encoded_passwords");

			}
		}
		return getTables(entity, l.get("_id")+"");
	}

}
