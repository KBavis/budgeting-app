package com.bavis.budgetapp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.UserRepository;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	private static Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public User create(User user) throws Exception {
		LOG.debug("Name Passed: " + user.getName());
		LOG.debug("Usernmae Passed " + user.getUsername());
		LOG.debug("Password Passed " + user.getPassword());
		LOG.debug("Profile Image Passed " + user.getProfileImage());
		LOG.debug("Creating User [{}]", user);
		return userRepository.save(user);
	}
}
