package com.bavis.budgetapp.service;

import java.util.Set;

import com.bavis.budgetapp.model.Transaction;

public interface ScraperService {
	Set<Transaction> getTransactions();
}
