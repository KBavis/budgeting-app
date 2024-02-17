package com.bavis.budgetapp.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.config.YAMLConfig;
import com.bavis.budgetapp.model.Transaction;
import com.bavis.budgetapp.service.ScraperService;

import io.github.cdimascio.dotenv.Dotenv;


@Service
public class ScraperServiceImpl implements ScraperService{
	private static final Logger LOG = LoggerFactory.getLogger(ScraperServiceImpl.class);
	
	@Autowired
	private YAMLConfig yamlConfig;
	
	@Override
	public Set<Transaction> getTransactions() {
		//TODO: Remove this logic and use encryption based on based in Logic from user
		Dotenv dotenv = Dotenv.load();
		
		try {
			System.setProperty("webdriver.chrome.driver", "C:\\ChromeDriver\\chromedriver.exe");
			ChromeOptions options = new ChromeOptions();
	        options.addArguments("--headless"); // Run Chrome in headless mode (without opening GUI)

	        // Initialize ChromeDriver
	        WebDriver driver = new ChromeDriver(options);
	        
	        driver.manage().window().maximize();
	        
	        String loginUrl = yamlConfig.getLoginUrl();
	        
	        driver.get(loginUrl);
	        
	        // Wait until the page is fully loaded
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60)); // Wait up to 10 seconds

	        // Check document.readyState property using JavaScript
	        wait.until(ExpectedConditions.urlToBe(loginUrl));
	        
	        if (driver.getCurrentUrl().equals(loginUrl)) {
	            System.out.println("Page loaded successfully: " + loginUrl);
	        } else {
	            System.out.println("Failed to load the page: " + loginUrl);
	        }
	        
	        boolean isUserNamePresent = isElementPresent(driver, By.id("userid-content"));
	        boolean isPasswordPresent = isElementPresent(driver, By.id("password-content"));
	        boolean isLoginButtonPresent = isElementPresent(driver, By.id("log-in-button"));
	        // Print result
	        if (isUserNamePresent && isPasswordPresent && isLoginButtonPresent) {
	            System.out.println("Element's with ID 'userid-content' and ID 'password-content' are present.");
	        } else {
	            System.out.println("Elements NOT present");
	        }
	        
	        wait.until(ExpectedConditions.elementToBeClickable(By.id("#userid-content")));
	        wait.until(ExpectedConditions.elementToBeClickable(By.id("#password-content")));
	        wait.until(ExpectedConditions.elementToBeClickable(By.id("#log-in-button")));
	        
	        driver.findElement(By.id("userid-content")).sendKeys(dotenv.get("USERNAME"));
	        driver.findElement(By.id("password-content")).sendKeys(dotenv.get("PASSWORD"));
	        

	        
	        LOG.info("Submitting login form ... ");
	        // Submit the login form
	        driver.findElement(By.id("log-in-button")).click();
	        LOG.info("User succesfully logged in!");
	        
	        LOG.info("New URL: " + driver.getCurrentUrl());
	        
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//Establish a @Connection to user's discover account 
		
		
		//Login to the users account 
		
		//Click the 'All Activity & Statements' button 
		
		//Click the 'Download' button undre 'Activity Summary' section 
		
		//Select the optioon to download a CSV 
		
		//Parse this CSV with Apache Library into @Transactions 
		return null;
	}
	
    // Method to check if an element is present on the page
	//TODO: Delete Mes
    private static boolean isElementPresent(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }
	
}
