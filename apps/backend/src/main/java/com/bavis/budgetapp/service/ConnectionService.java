package com.bavis.budgetapp.service;

import com.bavis.budgetapp.exception.ConnectionCreationException;
import com.bavis.budgetapp.entity.Connection;

/**
 * @author Kellen Bavis
 *
 * Service to house functionality regarding Connection entities
 */
public interface ConnectionService {
	/**
	 * Function utilized to establish a new Connection with an institution
	 *
	 * @param connection
	 * 			- Connection to be persisted within our database
	 * @return
	 * 			- Persisted Connection entity
	 * @throws ConnectionCreationException
	 * 			- thrown in the case that an error occurs while creating our Connection
	 */
	Connection create(Connection connection) throws ConnectionCreationException;

	/**
	 * Function utilized to update a Connection with updated properties
	 *
	 * @param connection
	 * 				- Connection with updated properties
	 * @param connectionId
	 * 				- ID corresponding to Connection entity to be updated
	 * @return
	 * 				- Updated Connection entity
	 */
	Connection update(Connection connection, Long connectionId);

	/**
	 * Function to fetch a specific Connection by ID from our database
	 *
	 * @param connectionId
	 * 			- ID corresponding to Connection entity to be fetched
	 * @return
	 * 			- Fetched Connection entity
	 */
	Connection read(Long connectionId);

	/**
	 * Function to delete a specific Connection by ID from our database
	 *
	 * @param connectionId
	 * 			- ID corresponding to Connection entity to be removed
	 */
	void delete(Long connectionId);
}
