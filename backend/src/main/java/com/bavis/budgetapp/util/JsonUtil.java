package com.bavis.budgetapp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Kellen Bavis
 *
 * Utility class to store all needed JSON Operations
 */
@Component
@Log4j2
public class JsonUtil {
    private final ObjectMapper _objectMapper;

    public JsonUtil(ObjectMapper _objectMapper) {
        this._objectMapper = _objectMapper;
    }

    /**
     * Functionality to transform an Object to JSON Format
     *
     * @param object
     *          - Object to be written to JSON Format
     * @return
     *          - Object written as JSON String
     */
    public String toJson(Object object) throws RuntimeException{
        try {
            return _objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e){
            log.error("Failed to convert the object [{}] to JSON due to following reason: {}", object.toString(), e.getMessage());
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    /**
     * TODO: This logic is specific to one use case. Should be generalized for other use cases
     *
     * Functionality to extract the balance of a specified account based on JSON response
     *
     * @param jsonString
     *          - JSON Response to extract account balance from
     * @param accountId
     *          - Account ID to locate specific balance for
     * @param balancePath
     *          - Path needed to extract our balance
     * @return
     *          - Extracted balance for Account
     */
    public Double extractBalanceByAccountId(String jsonString, String accountId, String balancePath) {
        try {
            log.info("Attempting to extract balance from response for the following Account ID: [{}]", accountId);
            log.info("Json String to parse for Account Balance: {}", jsonString);
            JsonNode rootNode = _objectMapper.readTree(jsonString);
            JsonNode accountsNode = rootNode.path("accounts");

            for (JsonNode accountNode : accountsNode) {
                String currentAccountId = accountNode.path("account_id").asText();
                if (currentAccountId.equals(accountId)) {
                    JsonNode balanceNode = accountNode.at(balancePath);
                    if (balanceNode.isMissingNode()) {
                        log.error("Balance attribute '{}' not found for account ID '{}'", balancePath, accountId);
                        return null;
                    }
                    return balanceNode.asDouble();
                }
            }

            log.error("Account ID '{}' not found in the JSON response", accountId);
        } catch (Exception e) {
            log.error("Error occurred while extracting balance for account ID '{}': {}", accountId, e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * Utility function to extract relevant message from FeignClientException for PlaidServiceException cleanliness
     *
     * @param e
     *      - Feign Client exception
     * @return
     *      - relevant error message regarding our PlaidClient
     */
    public String extractErrorMessage(FeignException.FeignClientException e){
        try {
            log.info("Attempting to extract relevant error message for following FeignClientException: [{}]", e.getMessage());
            String responseBody = e.contentUTF8();
            JsonNode jsonNode = _objectMapper.readTree(responseBody);
            String errorMsg = jsonNode.get("error_message").asText();
            log.debug("Relevant error message extracted from FeignClientException: [{}]", e.getMessage());
            return errorMsg;
        } catch (Exception ex) {
            // Fallback to returning the original exception message if parsing fails
            log.error("An exception occurred while extracting error message from FeignClientException: [{}]", ex.getMessage());
            return e.getMessage() != null ? e.getMessage() : "FeignClientException occurred. Unable to extract error message";
        }
    }


}
