package com.bavis.budgetapp.service;

import java.time.LocalDate;
import java.util.List;

import com.bavis.budgetapp.model.Transaction;

public interface CSVService {
	/**
	 * 
	 * @param fileName
	 * 			- file name of CSV containing transaction data 
	 * @param cutOffDate
	 * 			- cut off date to pull relevant data from 
	 * @return
	 */
	List<Transaction> getLatestTransactions(String fileName, LocalDate cutOffDate);
}
