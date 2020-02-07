package com.parminder.authentication.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.Column;
import com.parminder.authentication.bo.ColumnPermission;
import com.parminder.authentication.bo.Permissions;
import com.parminder.authentication.bo.Table;
import com.parminder.authentication.bo.TablePermission;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.User.UserType;
import com.parminder.authentication.repository.TablePermissionRepository;
import com.parminder.authentication.repository.TableRepository;

@RestController
public class TableController {

	@Autowired
	TableRepository tableRepository;

	@Autowired
	TablePermissionRepository tablePermissionRepository;

	@GetMapping(path = "/system_tables")
	public List<Table> getTables() {
		return tableRepository.findAll();
	}

	@GetMapping(path = "/system_tables/{id}")
	public Table getTables(@PathVariable String id) {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);

		Table table = tableRepository.findById(id).get();
		if (loggerInUser.getUserType() == UserType.SuperAdmin || loggerInUser.getUserType() == UserType.CompanyAdmin) {
			setBestPermission(table, null, loggerInUser);
		} else {
			TablePermission tp = tablePermissionRepository.findByClassIdAndParentId(new ObjectId(table.get_id()),
					new ObjectId(loggerInUser.getParent_id()));
			setBestPermission(table, tp, loggerInUser);
		}

		List<Table> tables = tableRepository.findByParentClass(table.get_id().toString());
		for (Table t : tables) {
			if (loggerInUser.getUserType() == UserType.SuperAdmin
					|| loggerInUser.getUserType() == UserType.CompanyAdmin) {
				setBestPermission(t, null, loggerInUser);
			} else {

				TablePermission ttp = tablePermissionRepository.findByClassIdAndParentId(new ObjectId(t.get_id()),
						new ObjectId(loggerInUser.getParent_id()));
				setBestPermission(t, ttp, loggerInUser);
			}
		}
		table.setChildTables(tables);
		return table;
	}

	@GetMapping(path = "/system_tables/name/{id}")
	public Table getTableByName(@PathVariable String id) {
		Table table = tableRepository.findByName(id);
		List<Table> tables = tableRepository.findByParentClass(table.get_id().toString());
		table.setChildTables(tables);
		return table;
	}

	@PostMapping(path = "/system_tables")
	public Table CreateTable(@RequestBody Table table) throws Exception {
		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		if (loggerInUser.getUserType() != UserType.SuperAdmin) {
			throw new Exception("Not Permitted");
		}
		Table alreadyTableExist = tableRepository.findByName(table.getName());
		if (alreadyTableExist != null && !alreadyTableExist.get_id().equals(table.get_id())) {
			throw new Exception("Already Exist With Same Name");
		}
		Set<String> s = new HashSet<String>();
		for (Column column : table.getColumns()) {
			if (!s.add(column.getName())) {
				throw new Exception("Duplicate Column :" + column.getName());
			}
		}
		;
		return tableRepository.save(table);
	}

	@GetMapping(path = "/system_tables/user")
	public List<Table> CreateTable() throws Exception {

		User loggerInUser = (User) RequestContextHolder.getRequestAttributes().getAttribute("user", 0);
		if (loggerInUser.getUserType() == UserType.SuperAdmin) {
			List<Table> tables = tableRepository.findByParentClass(null);

			for (Table tt : tables) {
				setBestPermission(tt, null, loggerInUser);
			}
			return tables;
		} else {
			List<Table> tables = tableRepository.findByParentClass(null);
			List<Table> rT = new ArrayList<Table>();

			for (Table table : tables) {

				List<Table> cT = tableRepository.findByParentClass(table.get_id().toString());
				for (Table CTT : cT) {
					TablePermission tp = tablePermissionRepository.findByClassIdAndParentId(new ObjectId(CTT.get_id()),
							new ObjectId(loggerInUser.getParent_id()));
					if (tp == null) {
						setBestPermission(CTT, tp, loggerInUser);
						table.addChildTables(CTT);
					} else {
						for (Entry<String, Permissions> entrySet : tp.getRolePermissions().entrySet()) {
							Permissions p = entrySet.getValue();
							String role = entrySet.getKey();
							if (role.equals("null") && p.isCanList()) {
								CTT = setBestPermission(CTT, tp, loggerInUser);
								table.addChildTables(CTT);

								break;
							} else if (loggerInUser.getRole().contains(role) && p.isCanList()) {
								CTT = setBestPermission(CTT, tp, loggerInUser);
								table.addChildTables(CTT);
								break;
							}
						}
					}

//					for (Permissions p : CTT.getPermissions()) {
//						if (p.getRole() == null) {
//							continue;
//						}
//						if (p.getRole().equals("*") && p.isCanList()) {
//							table.addChildTables(CTT);
//							break;
//						} else if (loggerInUser.getRole().contains(p.getRole()) && p.isCanList()) {
//							table.addChildTables(CTT);
//							break;
//						}
//					}
				}
				TablePermission tp = tablePermissionRepository.findByClassIdAndParentId(new ObjectId(table.get_id()),
						new ObjectId(loggerInUser.getParent_id()));
				if (tp == null) {
					setBestPermission(table, tp, loggerInUser);
					rT.add(table);
				} else {
					for (Entry<String, Permissions> entrySet : tp.getRolePermissions().entrySet()) {
						Permissions p = entrySet.getValue();
						String role = entrySet.getKey();
						if (role.equals("null") && p.isCanList()) {
							table = setBestPermission(table, tp, loggerInUser);
							rT.add(table);
							break;
						} else if (loggerInUser.getRole().contains(role)) {
							table = setBestPermission(table, tp, loggerInUser);
							rT.add(table);
							break;
						}
					}
				}
//				for (Permissions p : table.getPermissions()) {
//					if (p.getRole() == null) {
//						rT.add(table);
//						continue;
//					}
//					if (p.getRole().equals("*")) {
//						rT.add(table);
//						break;
//
//					} else if (loggerInUser.getRole().contains(p.getRole())) {
//						rT.add(table);
//						break;
//					}
//				}

			}

			return rT;
		}

	}

	private Table setBestPermission(Table cTT, TablePermission tp, User loggerInUser) {
		Permissions permissions = new Permissions();
		if (tp == null) {
			permissions.setRead(true);
			permissions.setDelete(true);
			permissions.setWrite(true);
			permissions.setCanAdd(true);
		} else {
			for (Entry<String, Permissions> entrySet : tp.getRolePermissions().entrySet()) {
				Permissions p = entrySet.getValue();
				String role = entrySet.getKey();
				if (role.equals("null") || loggerInUser.getRole().contains(role)) {
					if (p.isRead()) {
						permissions.setRead(p.isRead());
					}
					if (p.isDelete()) {
						permissions.setDelete(p.isDelete());
					}
					if (p.isWrite()) {
						permissions.setWrite(p.isWrite());
					}
					if (p.isCanAdd()) {
						permissions.setCanAdd(p.isCanAdd());
					}
				}
			}
		}

		cTT.setPermission(permissions);

		Map<String, ColumnPermission> columnPermission = new HashMap<String, ColumnPermission>();
		if (tp == null) {
			for (Column c : cTT.getColumns()) {
				ColumnPermission cp = new ColumnPermission();
				cp.setRead(true);
				cp.setWrite(true);
				columnPermission.put(c.getName(), cp);
			}
		} else {

			for (Entry<String, Map<String, ColumnPermission>> entrySet : tp.getColumnPermissions().entrySet()) {
				Map<String, ColumnPermission> p = entrySet.getValue();
				String role = entrySet.getKey();

				if (role.equals("null") || loggerInUser.getRole().contains(role)) {

					for (Entry<String, ColumnPermission> colemnEntrySet : p.entrySet()) {
						ColumnPermission cp = colemnEntrySet.getValue();
						String ck = colemnEntrySet.getKey();
						if (columnPermission.containsKey(ck)) {
							if (cp.isRead()) {
								columnPermission.get(ck).setRead(cp.isRead());
							}
							if (cp.isWrite()) {
								columnPermission.get(ck).setWrite(cp.isWrite());
							}
						} else {
							columnPermission.put(ck, cp);
						}
					}
				}
			}
		}
		cTT.setColumnPermissions(columnPermission);
		return cTT;
	}

}
