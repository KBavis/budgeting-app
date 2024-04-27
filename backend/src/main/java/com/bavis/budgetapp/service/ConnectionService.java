package com.bavis.budgetapp.service;

import com.bavis.budgetapp.exception.ConnectionCreationException;
import com.bavis.budgetapp.entity.Connection;

public interface ConnectionService {
	Connection create(Connection connection) throws ConnectionCreationException;
	Connection update(Connection connection);
	Connection read(Long connectionId);
	void delete(Long connectionId);
}
