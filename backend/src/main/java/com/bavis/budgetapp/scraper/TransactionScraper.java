package com.bavis.budgetapp.scraper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionScraper {
	
	@Bean
	public void scrape() {
		String url = "https://www.discover.com/";
		

		
		//Establish a @Connection to user's discover account 
		
		//Login to the users account 
		
		//Click the 'All Activity & Statements' button 
		
		//Click the 'Download' button undre 'Activity Summary' section 
		
		//Select the optioon to download a CSV 
		
		//Parse this CSV with Apache Library into @Transactions 
	}
}
