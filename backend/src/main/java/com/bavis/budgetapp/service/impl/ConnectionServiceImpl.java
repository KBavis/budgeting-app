package com.bavis.budgetapp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.ConnectionRepository;
import com.bavis.budgetapp.model.Connection;
import com.bavis.budgetapp.service.ConnectionService;

import lombok.RequiredArgsConstructor;

@Service
public class ConnectionServiceImpl implements ConnectionService{
	
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionServiceImpl.class);
	
	private final ConnectionRepository _repository;

	public ConnectionServiceImpl(ConnectionRepository connectionRepository){
		this._repository = connectionRepository;
	}

	@Override
	public Connection create(Connection connection) {
		LOG.info("Creating Connection [{}]", connection);
		return _repository.save(connection);
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
