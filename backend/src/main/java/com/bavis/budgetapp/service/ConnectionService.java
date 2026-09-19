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
	 * Flag a Connection as requiring the User's attention (i.e. Plaid reported ITEM_LOGIN_REQUIRED)
	 *
	 * @param connectionId
	 * 			- ID of the Connection to flag
	 * @param errorCode
	 * 			- Plaid's error code explaining why
	 * @param errorMessage
	 * 			- Plaid's error message explaining why
	 * @return
	 * 			- persisted Connection
	 */
	Connection markDisconnected(Long connectionId, String errorCode, String errorMessage);

	/**
	 * Mark a Connection as healthy again and clear any previously reported error (i.e. after the User re-authenticated
	 * via Plaid Link's update mode)
	 *
	 * @param connectionId
	 * 			- ID of the Connection to mark as connected
	 * @return
	 * 			- persisted Connection
	 */
	Connection markConnected(Long connectionId);

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
