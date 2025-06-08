package com.garage.feing;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;

import feign.Headers;
import feign.RequestLine;

public interface Webhook {

    @RequestLine("GET /webhook")
    @Headers("Content-Type: application/json")
    String sendEvent(@RequestBody Map<String, Object> map);
    
}
