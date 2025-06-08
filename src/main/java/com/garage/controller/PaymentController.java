package com.garage.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.garage.service.PaymentService;
import com.garage.service.SpotService;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<Object> revenue( @RequestBody String event ) {
        var response = paymentService.revenue(event);
        return ResponseEntity.status(Objects.nonNull(response.get("error")) ? HttpStatus.NOT_FOUND : HttpStatus.OK)
                .body(response);
    }   
    
}
