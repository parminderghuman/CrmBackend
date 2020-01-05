package com.parminder.authentication.controller;

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

import com.parminder.authentication.bo.Column;
import com.parminder.authentication.bo.Table;
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
		return tableRepository.findById(id).get();
	}
	
	@PostMapping(path = "/system_tables")
	public Table CreateTable(@RequestBody Table table) throws Exception {
		
		
		Set<String>  s = new HashSet<String>(); 
		
		for(Column column: table.getColumns()) {
			if(!s.add(column.getName())){
				throw new Exception("Duplicate Column :"+column.getName());
			}
		};
		return tableRepository.save(table);
		
	}
	
	
	
}
