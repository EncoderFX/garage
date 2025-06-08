package com.garage.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.garage.entity.Payment;
import com.garage.repository.EventRepository;
import com.garage.repository.GarageRepository;
import com.garage.repository.LeaseRepository;
import com.garage.repository.PaymentRepository;
import com.garage.repository.SpotRepository;
import com.garage.util.Middleware;

import reactor.core.publisher.Mono;

@Service
public class WebhookService {

    @Autowired
    private SpotRepository spotRepository;
    
    @Autowired
    private GarageRepository garageRepository;
    
    @Autowired
    private LeaseRepository leaseRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    public Mono<Void> processWebhook(String event) {
        Map<String, Object> events = new HashMap<>();
        events.put("originalEvent", event);
        
        return Mono.fromRunnable(() -> {
            Middleware.link(
                    new EventService(eventRepository, garageRepository, spotRepository),
                    new LeaseService(leaseRepository, eventRepository, spotRepository),
                    new PaymentService(paymentRepository, leaseRepository, eventRepository)
            ).process(events);

        });
    }

}
