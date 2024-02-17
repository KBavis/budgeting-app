package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.Connection;

public interface ConnectionService {
	Connection create(Connection connection);
	Connection update(Connection connection);
	Connection read(Long connectionId);
	void delete(Long connectionId);
}
