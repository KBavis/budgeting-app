package com.bavis.budgetapp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.UserRepository;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public User create(User user) throws Exception {
		LOG.debug("Creating User [{}]", user);
		return userRepository.save(user);
	}
}
