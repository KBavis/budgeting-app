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

    public String extractAttributeByPath(String jsonString, String attributePath) {
        try {
            JsonNode rootNode = _objectMapper.readTree(jsonString);
            String[] pathSegments = attributePath.split("\\.");

            JsonNode currentNode = rootNode;
            for (String segment : pathSegments) {
                if (currentNode.isArray()) {
                    // Handle array traversal
                    currentNode = searchInArray(currentNode, segment);
                    if (currentNode == null) {
                        return null;
                    }
                } else if (currentNode.isObject()) {
                    // Handle object traversal
                    currentNode = currentNode.get(segment);
                    if (currentNode == null) {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            return currentNode.asText();
        } catch (Exception e) {
            LOG.error("Error occurred while extracting attribute by path: {}", attributePath, e);
        }
        return null;
    }

    private JsonNode searchInArray(JsonNode arrayNode, String attributeName) {
        for (JsonNode node : arrayNode) {
            JsonNode attributeNode = node.get(attributeName);
            if (attributeNode != null) {
                return node;
            }
        }
        return null;
    }


}
