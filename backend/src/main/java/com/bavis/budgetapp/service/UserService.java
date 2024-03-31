package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.User;

public interface UserService {
	User create(User category);

	User readById(Long id);

	User readByUsername(String username);

	User update(Long id, User user);

	boolean existsByUsername(String username);

	User getCurrentAuthUser();
}
