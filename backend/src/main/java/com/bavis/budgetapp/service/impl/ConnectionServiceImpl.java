package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.exception.ConnectionCreationException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.ConnectionRepository;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.service.ConnectionService;

/**
 * @author Kellen Bavis
 *
 * Implementation of our Connection Service functionality
 */
@Service
@Log4j2
public class ConnectionServiceImpl implements ConnectionService{
	private final ConnectionRepository _repository;

	public ConnectionServiceImpl(ConnectionRepository connectionRepository){
		this._repository = connectionRepository;
	}

	@Override
	public Connection create(Connection connection) throws ConnectionCreationException{
		try{
			log.info("Creating Connection [{}]", connection);
			return _repository.save(connection);
		} catch(Exception e){
			log.error("An exception was thrown while attempting to create a Connection: [{}]", e.getMessage());
			throw new ConnectionCreationException(e.getMessage());
		}
	}


	@Override
	public Connection update(Connection connection, Long connectionId) throws RuntimeException{
		//Fetch Connection corresponding to ID
		Connection connectionToUpdate = _repository.findById(connectionId)
				.orElseThrow(() -> new RuntimeException("Unable to find Connection with ID " + connectionId + " to update."));

		connectionToUpdate.setPreviousCursor(connection.getPreviousCursor());
		connectionToUpdate.setLastSyncTime(connection.getLastSyncTime());
		if(!StringUtils.isBlank(connection.getOriginalCursor())){
			connectionToUpdate.setOriginalCursor(connection.getOriginalCursor()); //persist original cursor if passed
		}

		//Return persisted Connection
		return _repository.save(connectionToUpdate);
	}

	// TODO: finish this logic and add comments
	@Override
	public Connection read(Long connectionId) throws RuntimeException{
		log.info("Attempting to read a Connection entity with the ID {}", connectionId);
		return _repository.findById(connectionId)
				.orElseThrow(() -> new RuntimeException("Unable to locate Connection with ID " + connectionId));
	}

	// TODO: finish this logic and add comments
	@Override
	public void delete(Long connectionId) {

	}

}
