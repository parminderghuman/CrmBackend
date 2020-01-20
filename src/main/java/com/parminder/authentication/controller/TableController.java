package com.parminder.authentication.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import com.parminder.authentication.bo.Column;
import com.parminder.authentication.bo.Permissions;
import com.parminder.authentication.bo.Table;
import com.parminder.authentication.bo.User;
import com.parminder.authentication.bo.User.UserType;
import com.parminder.authentication.repository.TableRepository;

@RestController
public class TableController {

	@Autowired
	TableRepository tableRepository;

	@GetMapping(path = "/system_tables")
	public List<Table> getTables() {
		return tableRepository.findAll();
	}

	@GetMapping(path = "/system_tables/{id}")
	public Table getTables(@PathVariable String id) {
		Table table = tableRepository.findById(id).get();
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
		
		 User loggerInUser =   (User) RequestContextHolder.getRequestAttributes().getAttribute("user",0);
		 if(loggerInUser.getUserType() == UserType.SuperAdmin) {
			 List<Table> tables = tableRepository.findByParentClass(null);
			 Table  table = new Table();
			 table.setName("system_tables");
			tables.add(table);
			
			return tables;	
		 }else {	
			List<Table>  tables = tableRepository.findByParentClass(null);
			List<Table>  rT = new ArrayList<Table>();
			
			
			for(Table table : tables) {
				
				List<Table>  cT =	tableRepository.findByParentClass(table.get_id().toString());
				for(Table CTT : cT) {
					for(Permissions  p : CTT.getPermissions()) {
						if(p.getRole() == null) {
							continue;
						}
						if(p.getRole().equals("*") && p.isCanList()) {
							table.addChildTables(CTT);
							continue;
						}
						else if( loggerInUser.getRole().contains(p.getRole() ) && p.isCanList()) {
							table.addChildTables(CTT);	
							continue;
						}
					}					
				}
			
			
				for(Permissions  p : table.getPermissions()) {
					if(p.getRole() == null) {
						continue;
					}
					if(p.getRole().equals("*") 	) {
						rT.add(table);
						continue;
						
					}
					else if( loggerInUser.getRole().contains(p.getRole() ) ) {
						rT.add(table);
						continue;
					}
				}
			}
			
			return rT;	
		 }
		
		
	}
	
}
