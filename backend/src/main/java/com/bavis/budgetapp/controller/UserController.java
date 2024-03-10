package com.bavis.budgetapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * TODO: Finalize this implementation
 */
@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService _userSerivce;

	public UserController(UserService _userSerivce) {
		this._userSerivce = _userSerivce;
	}

	private static Logger LOG = LoggerFactory.getLogger(UserController.class);

	/**
	 * Endpoint to access all currently registered users
	 *
	 * @return
	 * 		- list of all registered users
	 */
	@GetMapping
	public List<User> readAll() {
		return null;
	}

	/**
	 * Endpoint to access a specified user by ID
	 *
	 * @return
	 * 		- user associated with a particular ID
	 */
	@GetMapping("/{id}")
	public User read(@PathVariable Long id) {
		return null;
	}


}
