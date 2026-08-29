package com.bavis.budgetapp.util;

import com.bavis.budgetapp.TestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.FeignException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
@Log4j2
public class JsonUtilTests {
    @Mock
    private ObjectMapper mockObjectMapper;

    @InjectMocks
    private JsonUtil jsonUtil;

    private ObjectMapper testObjectMapper;

    @BeforeEach
    void setUp() {
        testObjectMapper = new ObjectMapper();
    }


    /**
     * Test Class for Testing JSON Util
     */
    private class Person {
        private String name;
        private int age;
       public Person(String name, int age){
            this.name = name;
            this.age = age;
       }
    }

    @Test
    public void testToJson_ValidObject_Successful() throws Exception{
        //Arrange
        Person person = new Person("Test Person", 12);
        String expectedJson = "{\"name\":\"Test Person\",\"age\":12}";

        //Mock
        when(mockObjectMapper.writeValueAsString(person)).thenReturn(expectedJson);

        //Act
        String actualJson = jsonUtil.toJson(person);

        //Assert
        assertNotNull(actualJson);
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testToJson_InvalidObject_Failure() throws Exception {
        //Arrange
        Object object = new Object();
        String errorMessage = "Failed to convert object to JSON";

        //Mock
        when(mockObjectMapper.writeValueAsString(object)).thenThrow(new RuntimeException(errorMessage));


        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> jsonUtil.toJson(object));
        assertEquals("Failed to convert object to JSON", exception.getMessage());
    }

    @Test
    public void extractBalanceByAccountId_ValidAccountId_Successful() throws JsonProcessingException {
        // Arrange
        String accountId = "123456";
        double balance = 1000.0;
        String jsonString = testObjectMapper.writeValueAsString(TestHelper.createBalanceResponse(accountId, balance));
        ObjectNode rootNode = testObjectMapper.createObjectNode();
        ArrayNode accountsNode = testObjectMapper.createArrayNode();
        ObjectNode accountNode = testObjectMapper.createObjectNode();
        accountNode.put("account_id", accountId);
        ObjectNode balancesNode = testObjectMapper.createObjectNode();
        balancesNode.put("available", balance);
        accountNode.set("balances", balancesNode);
        accountsNode.add(accountNode);
        rootNode.set("accounts", accountsNode);

        //Mock
        when(mockObjectMapper.readTree(jsonString)).thenReturn(rootNode);

        // Act
        Double extractedBalance = jsonUtil.extractBalanceByAccountId(jsonString, accountId, "/balances/available");

        // Assert
        assertEquals(balance, extractedBalance);
    }

    @Test
    public void extractBalanceByAccountId_NullJsonString_Failure() throws Exception {
        //Arrange
        String nullJsonString = "";
        String accountId = "123456";
        double balance = 1000.0;
        String errorMsg = "Error occurred while extracting balance for account ID '123456' : JsonProcessingException occurred";

        //Mock
        when(mockObjectMapper.readTree(nullJsonString)).thenThrow(new RuntimeException(errorMsg));

        //Act
        Double actualBalance = jsonUtil.extractBalanceByAccountId(nullJsonString, accountId, "/balances/available");

        //Assert
        assertNull(actualBalance); //NOTE: Logs and returns null for PlaidService to handle exception
    }

    @Test
    public void extractBalanceByAccountId_InvalidAccountId_Failure() throws Exception {
        //Arrange
        double balance = 1000.0;
        String invalidAccountId = "1";
        String correctAccountId = "123456";
        String jsonString = testObjectMapper.writeValueAsString(TestHelper.createBalanceResponse(correctAccountId, balance));
        ObjectNode rootNode = testObjectMapper.createObjectNode();
        ArrayNode accountsNode = testObjectMapper.createArrayNode();
        ObjectNode accountNode = testObjectMapper.createObjectNode();
        accountNode.put("account_id", correctAccountId);
        ObjectNode balancesNode = testObjectMapper.createObjectNode();
        balancesNode.put("available", balance);
        accountNode.set("balances", balancesNode);
        accountsNode.add(accountNode);
        rootNode.set("accounts", accountsNode);

        //Mock
        when(mockObjectMapper.readTree(jsonString)).thenReturn(rootNode);

        //Act
        Double actualBalance = jsonUtil.extractBalanceByAccountId(jsonString, invalidAccountId, "/balances/available");

        //Assert
        assertNull(actualBalance); //NOTE: Exception thrown by PlaidService Downstream. Only log and return null
    }

    @Test
    public void testExtractErrorMessage_ValidFeignException_Successful() throws Exception{
        //Arrange
        String expectedErrorMessage = "the following fields are invalid: access_token";
        String responseBody = "{\"display_message\":null,\"documentation_url\":\"https://plaid.com/docs/?ref=error#invalid-request-errors\",\"error_code\":\"INVALID_REQUEST\",\"error_message\":\"the following fields are invalid: access_token\",\"error_type\":\"INVALID_REQUEST\",\"request_id\":\"kqhaK3evRpSoNxY\",\"suggested_action\":null}";
        ObjectNode rootNode = testObjectMapper.createObjectNode();
        rootNode.put("error_message", "the following fields are invalid: access_token");


        //Mock
        FeignException.FeignClientException exception = mock(FeignException.FeignClientException.class);
        when(exception.contentUTF8()).thenReturn(responseBody);
        when(mockObjectMapper.readTree(responseBody)).thenReturn(rootNode);

        //Act
        String actualErrorMessage = jsonUtil.extractErrorMessage(exception);

        //Assert
        assertNotNull(actualErrorMessage);
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testExtractErrorMessage_NullFeignException_Failure() throws Exception {
       //Arrange
        String expectedErrorMessage = "FeignClientException occurred. Unable to extract error message";

        //Mock
        FeignException.FeignClientException exception = mock(FeignException.FeignClientException.class);
        when(exception.contentUTF8()).thenReturn(null);
        when(exception.getMessage()).thenReturn(null);
        when(mockObjectMapper.readTree("")).thenThrow(JsonProcessingException.class);

        //Act
        String errorMessage = jsonUtil.extractErrorMessage(exception);

        //Assert
        assertNotNull(errorMessage);
        assertEquals(expectedErrorMessage, errorMessage);
    }

    @Test
    public void testExtractErrorMessage_InvalidFeignException_Failure() throws Exception {
        //Arrange
        String expectedErrorMessage = "{\"display_message\":null,\"documentation_url\":\"https://plaid.com/docs/?ref=error#invalid-request-errors\",\"error_code\":\"INVALID_REQUEST\",\"error_message\":\"the following fields are invalid: access_token\",\"error_type\":\"INVALID_REQUEST\",\"request_id\":\"kqhaK3evRpSoNxY\",\"suggested_action\":null}";
        ObjectNode rootNode = testObjectMapper.createObjectNode();


        //Mock
        FeignException.FeignClientException exception = mock(FeignException.FeignClientException.class);
        when(exception.contentUTF8()).thenReturn(expectedErrorMessage);
        when(mockObjectMapper.readTree(expectedErrorMessage)).thenReturn(rootNode);
        when(exception.getMessage()).thenReturn(expectedErrorMessage);


        //Act
        String errorMessage = jsonUtil.extractErrorMessage(exception);

        //Assert
        assertEquals(expectedErrorMessage, errorMessage);
    }


}
