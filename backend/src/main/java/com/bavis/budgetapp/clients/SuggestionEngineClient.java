package com.bavis.budgetapp.clients;

import com.bavis.budgetapp.dto.CategorySuggestionRequest;
import com.bavis.budgetapp.dto.CategorySuggestionResponse;
import com.bavis.budgetapp.dto.UncategorizedSuggestionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class SuggestionEngineClient {

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    @Value("${suggestion-engine.base-url")
    private String baseUrl;

    public SuggestionEngineClient() {
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Make HTTP request to Python Server to get Category prediction based on Transaction metadata
     *
     * @param request
     *          - metadata about Transaction and user
     * @return
     *          - suggest Category ID
     */
    public Long predictCategory(CategorySuggestionRequest request) throws JsonProcessingException {
        String jsonPayload = mapper.writeValueAsString(request);
        String url = baseUrl + "/suggestion";

        RequestBody body = RequestBody.create(
                jsonPayload, MediaType.parse("application/json")
        );

        Request pythonBackendRequest = new Request.Builder()
                .url(url)
                .post(body)
                .build();


        String errorMsg = "An exception occurred while making request to Python server to retrieve Category Suggestion";
        try (Response response = client.newCall(pythonBackendRequest).execute()){
            if (!response.isSuccessful()){
                log.error(errorMsg);
                throw new RuntimeException("Unable to retrieve Category Suggestion via following request: " + request);
            }

            String json = response.body().string();
            try {
                //TODO: Consider logging out additional data
                CategorySuggestionResponse suggestion = mapper.readValue(json, CategorySuggestionResponse.class);
                log.info("Category Suggestion for Transaction {} is {}, with a {}% confidence", request.getTransactionMetadata(), suggestion.getCategoryId(), suggestion.getConfidence());
                return suggestion.getCategoryId();
            } catch (JsonProcessingException e) {
                // fallback is an UncategorizedSuggestion (not enough data to make accurate prediction)
                UncategorizedSuggestionResponse uncategorizedResponse = mapper.readValue(json, UncategorizedSuggestionResponse.class);

                //TODO: figure out better way to handle this
                log.info("Failed to categorize Transaction: {}\nReasons: {}\nSuggested Actions: {}", request.getTransactionMetadata(), uncategorizedResponse.getReasons(), uncategorizedResponse.getSuggestedActions());
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve Category Suggestion: " + e.getMessage());
        }


    }

}
