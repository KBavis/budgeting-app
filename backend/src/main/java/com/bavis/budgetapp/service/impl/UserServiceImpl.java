package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.exception.UserNotFoundException;
import com.bavis.budgetapp.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.UserRepository;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.UserService;

import lombok.RequiredArgsConstructor;

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
	public User read(Long id) throws UserNotFoundException {
		return _userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
	}


	@Override
	public User update(Long id, User updatedUser) throws UserNotFoundException{
		User foundUser = read(id);
		_userMapper.updateUserProfile(foundUser, updatedUser);
		return _userRepository.save(foundUser);
	}

	@Override
	public boolean existsByUsername(String username){
		return _userRepository.existsByUsername(username);
	}
}
