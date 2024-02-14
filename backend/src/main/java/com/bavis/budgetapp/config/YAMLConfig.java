package com.bavis.budgetapp.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@EnableConfigurationProperties
@ConfigurationProperties(prefix="spring")
@Getter
@Setter
@Configuration
public class YAMLConfig {
	private Map<String,Double> categories;
}
