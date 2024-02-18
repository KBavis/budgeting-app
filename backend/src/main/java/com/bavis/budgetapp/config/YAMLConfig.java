package com.bavis.budgetapp.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@EnableConfigurationProperties
@ConfigurationProperties(prefix="spring")
@Getter
@Setter
@Configuration
@PropertySource("classpath:application.yaml")
public class YAMLConfig {
	private Map<String,Double> categories;
	
	@Value("${spring.csv.location}")
	private String csvDir;
	
//	private Map<String, Map<String, String>> account;
}
