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

	Long getUserIdByAccountIds(List<String> accountIds);

	User readById(Long id);

	User readByUsername(String username);

	User update(Long id, User user);

	boolean existsByUsername(String username);

	User getCurrentAuthUser();

	void removeCategory(Category category);

	List<User> readAll();

	/**
	 * Determine whether the currently authenticated User is the User with the specified ID.
	 * Used to enforce that a User can only access entities that they own.
	 *
	 * @param userId
	 * 			- ID of the User that owns an entity (may be null for unowned entities)
	 * @return
	 * 			- true only if userId is non-null and matches the currently authenticated User
	 */
	boolean isCurrentAuthUser(Long userId);
}
