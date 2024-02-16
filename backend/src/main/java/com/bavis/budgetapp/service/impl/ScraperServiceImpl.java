package com.bavis.budgetapp.service.impl;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.config.YAMLConfig;
import com.bavis.budgetapp.model.Transaction;
import com.bavis.budgetapp.service.ScraperService;

@Service
public class ScraperServiceImpl implements ScraperService{
	private static final Logger LOG = LoggerFactory.getLogger(ScraperServiceImpl.class);
	
	@Autowired
	private YAMLConfig yamlConfig;
	
	@Override
	public Set<Transaction> getTransactions() {
		Map<String, String> discoverUrls = yamlConfig.getAccount().get("discover");
		LOG.info("Discover Login URL : " + discoverUrls.get("Login"));
		LOG.info("Discover Home URL : " + discoverUrls.get("Home"));
		LOG.info("Discover Statements URL : " + discoverUrls.get("Statements"));
		
		//Establish a @Connection to user's discover account 
		
		//Login to the users account 
		
		//Click the 'All Activity & Statements' button 
		
		//Click the 'Download' button undre 'Activity Summary' section 
		
		//Select the optioon to download a CSV 
		
		//Parse this CSV with Apache Library into @Transactions 
		return null;
	}
	
}
