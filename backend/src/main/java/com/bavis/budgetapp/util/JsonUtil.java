package com.bavis.budgetapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JsonUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);
    private final ObjectMapper _objectMapper;

    public JsonUtil(ObjectMapper _objectMapper) {
        this._objectMapper = _objectMapper;
    }

    public <T> T fromJson(String json, Class<T> klass) {
        try {
            return _objectMapper.readValue(json, klass);
        } catch(Exception e) {
           LOG.error("Failed To Map Json [" + json + "] to Class " + klass.toString(), e.getMessage());
        }
        return null;
    }

    public String toJson(Object object){
        try {
            return _objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e){
            LOG.error("Failed to convert the object [{}] to JSON due to following reason: {}", object.toString(), e.getMessage());
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    public String extractAttribute(String jsonString, String attributeName) {
        try{
            JsonNode jsonNode = _objectMapper.readTree(jsonString);
            JsonNode attributeNode = jsonNode.get(attributeName);

            if(attributeNode != null) {
                return attributeNode.asText();
            } else {
                LOG.error("Attribute [" + attributeName +"] does not exist in JSON Object [{}]", jsonString);
                return null;
            }
        } catch (Exception e) {
            LOG.error("Error Occured While Extracting Attribute [{}] from Json String [{}]", attributeName, jsonString);
        }
        return null;
    }

    public Double extractBalanceByAccountId(String jsonString, String accountId, String balancePath) {
        try {
            JsonNode rootNode = _objectMapper.readTree(jsonString);
            JsonNode accountsNode = rootNode.path("accounts");

            for (JsonNode accountNode : accountsNode) {
                String currentAccountId = accountNode.path("account_id").asText();
                if (currentAccountId.equals(accountId)) {
                    JsonNode balanceNode = accountNode.at(balancePath);
                    if (balanceNode.isMissingNode()) {
                        LOG.error("Balance attribute '{}' not found for account ID '{}'", balancePath, accountId);
                        return null;
                    }
                    return balanceNode.asDouble();
                }
            }

            LOG.error("Account ID '{}' not found in the JSON response", accountId);
        } catch (Exception e) {
            LOG.error("Error occurred while extracting balance for account ID '{}': {}", accountId, e.getMessage());
        }
        return null;
    }


}
