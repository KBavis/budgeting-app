package com.bavis.budgetapp.util;

import com.bavis.budgetapp.helper.TestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
@Log4j2
public class JsonUtilTests {
    @Mock
    private ObjectMapper mockObjectMapper;

    @InjectMocks
    private JsonUtil jsonUtil;

    private TestHelper testHelper;
    private ObjectMapper testObjectMapper;

    @BeforeEach
    void setUp() {
        testHelper = new TestHelper();
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
    void extractBalanceByAccountId_ValidAccountId_ReturnsBalance() throws JsonProcessingException {
        // Arrange
        String accountId = "123456";
        double balance = 1000.0;
        String jsonString = testObjectMapper.writeValueAsString(testHelper.createBalanceResponse(accountId, balance));
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
}
