package com.garage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.garage.service.WebhookService;

import reactor.core.publisher.Mono;

@RestController
public class WebhookController {

    private final WebhookService webhookService;

    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/webhook")
    public Mono<HttpStatus> handleWebhook( @RequestBody String event ) {
        return webhookService.processWebhook(event)
                .then(Mono.just(HttpStatus.ACCEPTED));
    }   
}
