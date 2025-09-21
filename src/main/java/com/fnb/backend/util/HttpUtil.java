package com.fnb.backend.util;

import com.fnb.backend.controller.domain.response.CustomResponse;

import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.io.IOException;

public class HttpUtil<T> implements HttpResponse.BodyHandler<T>{

    private final CustomResponse targetClass;

    public HttpUtil(CustomResponse targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
        // Read the response body as a string
        HttpResponse.BodySubscriber<String> bodySubscriber = HttpResponse.BodyHandlers.ofString().apply(responseInfo);

        // Transform the string body into a T object
        return HttpResponse.BodySubscribers.mapping(bodySubscriber, (String body) -> {
            try {
                return objectMapper.readValue(body, targetClass);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
    
}
