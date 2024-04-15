package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.UserRepository;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	private static Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	private final UserRepository _userRepository;

	private final UserMapper _userMapper;

	public UserServiceImpl(UserRepository _userRepository, UserMapper _userMapper) {
		this._userRepository = _userRepository;
		this._userMapper = _userMapper;
	}

	@Override
	public User create(User user){
		LOG.debug("Creating User [{}]", user);
		return _userRepository.save(user);
	}

	@Override
	public User readById(Long id) throws UserServiceException {
		return _userRepository.findById(id)
				.orElseThrow(() -> new UserServiceException("Could not find user with the ID" + id));
	}

	@Override
	public User readByUsername(String username) throws UserServiceException {
		return _userRepository.findByUsername(username)
				.orElseThrow(() -> new UserServiceException("Could not find user with the username " + username));
	}


	@Override
	public User update(Long id, User updatedUser) throws UserServiceException {
		User foundUser = readById(id);
		_userMapper.updateUserProfile(foundUser, updatedUser);
		return _userRepository.save(foundUser);
	}

	@Override
	public boolean existsByUsername(String username){
		return _userRepository.existsByUsername(username);
	}

	@Override
	public User getCurrentAuthUser(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName().trim();
		User user = _userRepository.findByUsername(username)
				.orElseThrow(() -> new UserServiceException("Could not find user with the username " + username));
		return  user;
	}
}
