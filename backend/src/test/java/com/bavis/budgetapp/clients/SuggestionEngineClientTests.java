package com.bavis.budgetapp.clients;


import com.bavis.budgetapp.dto.CategorySuggestionRequest;
import com.bavis.budgetapp.dto.TransactionMetadata;
import com.bavis.budgetapp.model.PlaidDetailedCategory;
import com.bavis.budgetapp.model.PlaidPrimaryCategory;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles(profiles = "test")
public class SuggestionEngineClientTests {

    private static MockWebServer mockWebServer;

    private SuggestionEngineClient client;

    @BeforeAll
    static void setupServer() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutdownServer() throws Exception {
        mockWebServer.close();
    }

    @BeforeEach
    void setupClient() {
        client = new SuggestionEngineClient();
        ReflectionTestUtils.setField(client, "baseUrl", mockWebServer.url("/").toString());
    }

    @Test
    public void testPredictCategorySuccess() throws Exception {
        String mockJson = """
            {
              "category_id": 42,
              "confidence": 96.78,
              "source" : "PERSONAL_MODEL",
              "reasoning": "Prediction"
            }
        """;

        mockWebServer.enqueue(new MockResponse.Builder()
                .body(mockJson)
                .build());

        CategorySuggestionRequest request = CategorySuggestionRequest.builder()
                .userId(1L)
                .transactionMetadata(TransactionMetadata.builder()
                        .amount(100.0)
                        .dateTime(LocalDateTime.now())
                        .merchant("Starbucks")
                        .plaidPrimaryCategory(PlaidPrimaryCategory.FOOD_AND_DRINK)
                        .plaidDetailedCategory(PlaidDetailedCategory.FOOD_AND_DRINK_FAST_FOOD)
                        .build())
                .build();

        Long categoryId = client.predictCategory(request);

        assertEquals(42L, categoryId);
    }

    @Test
    public void testPredictCategoryUncategorizedSuggestion() throws Exception {
        String mockJson = """
            {
              "type": "uncategorized",
              "reasons": ["Venmo transactions cannot be categorized"],
              "suggested_actions": ["Don't do venmo OMG!!"]
            }
        """;

        mockWebServer.enqueue(new MockResponse.Builder()
                .body(mockJson)
                .build());

        CategorySuggestionRequest request = CategorySuggestionRequest.builder()
                .userId(1L)
                .transactionMetadata(TransactionMetadata.builder()
                        .amount(100.0)
                        .dateTime(LocalDateTime.now())
                        .merchant("Starbucks")
                        .plaidPrimaryCategory(PlaidPrimaryCategory.FOOD_AND_DRINK)
                        .plaidDetailedCategory(PlaidDetailedCategory.FOOD_AND_DRINK_FAST_FOOD)
                        .build())
                .build();

        Long categoryId = client.predictCategory(request);

        assertNull(categoryId);
    }

    @Test
    public void testPredictCategoryFailedReqeust() throws Exception {
        String mockJson = """
            {
              "type": "uncategorized",
              "reasons": ["Venmo transactions cannot be categorized"],
              "suggested_actions": ["Don't do venmo OMG!!"]
            }
        """;

        mockWebServer.enqueue(new MockResponse.Builder()
                        .code(500)
                .build());

        CategorySuggestionRequest request = CategorySuggestionRequest.builder()
                .userId(1L)
                .transactionMetadata(TransactionMetadata.builder()
                        .amount(100.0)
                        .dateTime(LocalDateTime.now())
                        .merchant("Starbucks")
                        .plaidPrimaryCategory(PlaidPrimaryCategory.FOOD_AND_DRINK)
                        .plaidDetailedCategory(PlaidDetailedCategory.FOOD_AND_DRINK_FAST_FOOD)
                        .build())
                .build();

        assertThrows(RuntimeException.class, () -> client.predictCategory(request));
    }
}