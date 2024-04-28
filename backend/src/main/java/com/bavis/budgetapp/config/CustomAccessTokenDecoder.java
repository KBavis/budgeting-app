package com.bavis.budgetapp.config;

import com.bavis.budgetapp.response.AccessTokenResponse;
import com.bavis.budgetapp.response.LinkTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Customer Decoder for Debugging Plaid Response
 */
@Log4j2
public class CustomAccessTokenDecoder implements Decoder {
    private final ObjectMapper objectMapper;

    public CustomAccessTokenDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        String responseBody = Util.toString(response.body().asReader(Util.UTF_8));
        log.info("Raw response body using Feign Util: {}", responseBody);

        if (type == AccessTokenResponse.class) {
            return objectMapper.readValue(responseBody, AccessTokenResponse.class);
        } else if (type == LinkTokenResponse.class) {
            return objectMapper.readValue(responseBody, LinkTokenResponse.class);
        } else if (type == String.class) {
            return responseBody;
        } else {
            throw new DecodeException(response.status(), "Unsupported response type: " + type, response.request());
        }
    }
}
