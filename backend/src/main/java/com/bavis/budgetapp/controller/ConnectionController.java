package com.bavis.budgetapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bavis.budgetapp.model.Connection;
import com.bavis.budgetapp.service.ConnectionService;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author ADMIN
 * 
 * @TODO: Consider removal of Connection Serivce, Controller, and Repo if not connecting account 
 * @TODO: Remove CSVService from this logic, as this should be run on script by self 
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/connect")
public class ConnectionController {
	private final ConnectionService connectionService;
	private static Logger LOG = LoggerFactory.getLogger(ConnectionController.class);
	
	@PostMapping
	public Connection create(@RequestBody Connection connection) {

		return null;
//		try {
//			return service.create(connection);
//		} catch (Exception e) {
//			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access - unable to create connection");
//		}
	}
}
