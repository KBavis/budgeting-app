package com.bavis.budgetapp.service;

import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.User;

import java.util.List;

/**
 * User Service for storing functionality that interacts with our User entity
 *
 * @author Kellen Bavis
 */
public interface UserService {
	User create(User category);

	User readById(Long id);

	User readByUsername(String username);

	User update(Long id, User user);

	boolean existsByUsername(String username);

	User getCurrentAuthUser();

	void removeCategory(Category category);

	List<User> readAll();
}
