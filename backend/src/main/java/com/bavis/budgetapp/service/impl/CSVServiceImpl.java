package com.bavis.budgetapp.service.impl;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.config.YAMLConfig;
import com.bavis.budgetapp.model.Transaction;
import com.bavis.budgetapp.service.CSVService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CSVServiceImpl implements CSVService{
	private static Logger LOG = LoggerFactory.getLogger(CSVServiceImpl.class);
	
	private final YAMLConfig yamlConfig;

	@SuppressWarnings("deprecation")
	@Override
	public List<Transaction> getLatestTransactions(String fileName, LocalDate cutOffDate) {
		List<Transaction> recentTransactions = new ArrayList<>(); 
		try (Reader reader = new FileReader(yamlConfig.getCsvDir() + fileName)) {
			CSVParser csvParser = new  CSVParser(reader, CSVFormat.EXCEL.withFirstRecordAsHeader());
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			
			for(CSVRecord csvRecord: csvParser) {
                String transDateStr = csvRecord.get("Trans. Date");
                LocalDate transDate = LocalDate.parse(transDateStr, dateFormatter);
                String name = csvRecord.get("Description");
                double amount = Double.parseDouble(csvRecord.get("Amount"));
                String category = csvRecord.get("Category");
                
                Transaction transaction = Transaction.builder()
                		.accountSource(null)
                		.amount(amount)
                		.category(null)
                		.date(transDate)
                		.name(name)
                		.build();
                
                recentTransactions.add(transaction);
                LOG.info("Transaction Added [+]: [{}]", transaction.toString());
			}
			return recentTransactions;
			
		}  catch(IOException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
}
