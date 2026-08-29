package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.UserRepository;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Implementation of our User Service functionality
 */
@Service
@Log4j2
public class UserServiceImpl implements UserService{

	private final UserRepository _userRepository;

	private final UserMapper _userMapper;

	public UserServiceImpl(UserRepository _userRepository, UserMapper _userMapper) {
		this._userRepository = _userRepository;
		this._userMapper = _userMapper;
	}

	@Override
	public User create(User user){
		log.info("Attempting to create the following User: [{}]", user);
		return _userRepository.save(user);
	}

	@Override
	public Long getUserIdByAccountIds(List<String> accountIds) {
		List<Long> userIds = _userRepository.findUserIdByAccountIds(accountIds);

		if (userIds != null && userIds.size() > 1) {
			throw new UserServiceException("Found multiple user IDs corresponding to account ids");
		}

		return userIds != null ? userIds.get(0) : null;
	}

	@Override
	public List<User> readAll() {
		return _userRepository.findAll();
	}

	@Override
	public User readById(Long id) throws UserServiceException {
		log.info("Attempting to read a User by the following user ID: {}", id);
		return _userRepository.findById(id)
				.orElseThrow(() -> new UserServiceException("Could not find user with the ID " + id));
	}

	@Override
	public User readByUsername(String username) throws UserServiceException {
		log.info("Attempting to read a User by the following username: {}", username);
		return _userRepository.findByUsername(username)
				.orElseThrow(() -> new UserServiceException("Could not find user with the username " + username));
	}


	@Override
	public User update(Long id, User updatedUser) throws UserServiceException {
		log.info("Attempting to update User with ID {} with the following updated User: [{}]", id, updatedUser);
		User foundUser = readById(id);
		_userMapper.updateUserProfile(foundUser, updatedUser);
		return _userRepository.save(foundUser);
	}

	@Override
	public boolean existsByUsername(String username){
		return _userRepository.existsByUsername(username);
	}

	@Override
	public User getCurrentAuthUser() throws UserServiceException{
		log.info("Attempting to extract the currently authenticated User");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null) {
			log.error("No Authenticated User was found!");
			throw new UserServiceException("Unable to find any Authenticated user");
		}

		String username = authentication.getName().trim();
        return readByUsername(username);
	}

	@Override
	public void removeCategory(Category category) {
		log.info("Removing the following Category corresponding to ID {}", category.getCategoryId());
		User authUser = getCurrentAuthUser();
		List<Category> userCategories = authUser.getCategories() != null ? new ArrayList<>(authUser.getCategories()) : new ArrayList<>();

        userCategories.remove(category);
        authUser.setCategories(userCategories);
        List<Long> categoryIds = userCategories.stream().map(Category::getCategoryId).toList();
        log.info("Updated User {} to contain only the following Category IDs: [{}]", authUser.getUsername(), categoryIds);
    }
}
