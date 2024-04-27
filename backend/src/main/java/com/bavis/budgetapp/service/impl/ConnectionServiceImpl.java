package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.exception.ConnectionCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.ConnectionRepository;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.service.ConnectionService;

@Service
public class ConnectionServiceImpl implements ConnectionService{
	
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionServiceImpl.class);
	
	private final ConnectionRepository _repository;

	public ConnectionServiceImpl(ConnectionRepository connectionRepository){
		this._repository = connectionRepository;
	}

	@Override
	public Connection create(Connection connection) throws ConnectionCreationException{
		try{
			LOG.info("Creating Connection [{}]", connection);
			return _repository.save(connection);
		} catch(Exception e){
			throw new ConnectionCreationException(e.getMessage());
		}
	}

	@Override
	public Connection update(Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Connection read(Long connectionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Long connectionId) {
		// TODO Auto-generated method stub
		
	}

}
