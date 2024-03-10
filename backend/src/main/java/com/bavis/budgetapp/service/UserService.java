package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.User;

public interface UserService {
	User create(User category);

	User read(Long id);

	boolean existsByUsername(String username);
}
