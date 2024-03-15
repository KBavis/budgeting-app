package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.User;

public interface UserService {
	User create(User category);

	User read(Long id);

	User update(Long id, User user);

	boolean existsByUsername(String username);
}
