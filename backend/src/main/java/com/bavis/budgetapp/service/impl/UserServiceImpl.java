package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.UserRepository;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	private static Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public User create(User user){
		LOG.debug("Creating User [{}]", user);
		return userRepository.save(user);
	}

	@Override
	public User read(Long id) throws UserNotFoundException {
		return userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
	}

	@Override
	public boolean existsByUsername(String username){
		return userRepository.existsByUsername(username);
	}
}
