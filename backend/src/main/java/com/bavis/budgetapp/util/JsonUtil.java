package com.bavis.budgetapp.util;

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


}
