package com.bavis.budgetapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bavis.budgetapp.model.Connection;
import com.bavis.budgetapp.service.ConnectionService;
import com.bavis.budgetapp.service.ScraperService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/connect")
public class ConnectionController {
	private final ConnectionService connectionService;
	private final ScraperService scraperService; 
	private static Logger LOG = LoggerFactory.getLogger(ConnectionController.class);
	
	@PostMapping
	public Connection create(@RequestBody Connection connection) {
		LOG.debug("Recieved Connection creation request for [{}]", connection);
		
		scraperService.getTransactions();
		
		return null;
		
		
//		try {
//			return service.create(connection);
//		} catch (Exception e) {
//			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access - unable to create connection");
//		}
	}
}
