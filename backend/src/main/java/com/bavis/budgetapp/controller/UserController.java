package com.bavis.budgetapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	private final UserService userService;
	private static Logger LOG = LoggerFactory.getLogger(UserController.class);
	
	@PostMapping
	public User create(@RequestBody User user) {
		LOG.debug("Recieved User creation request for [{}]", user);
		
		try {
			return userService.create(user);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access - unable to create parent category");
		}
	}
}
