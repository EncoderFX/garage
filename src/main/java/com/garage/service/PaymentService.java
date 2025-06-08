package com.garage.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.garage.domain.Ciclo;
import com.garage.domain.EventyType;
import com.garage.dto.EventExitGarageDto;
import com.garage.entity.Event;
import com.garage.entity.Lease;
import com.garage.entity.Payment;
import com.garage.entity.Spot;
import com.garage.repository.EventRepository;
import com.garage.repository.LeaseRepository;
import com.garage.repository.PaymentRepository;
import com.garage.util.Middleware;
import com.garage.util.RealTime;

@Service
public class PaymentService extends Middleware {

    private final PaymentRepository paymentRepository;
    private final LeaseRepository leaseRepository;
    private final EventRepository eventRepository;
    
    private Lease lease;
    private LocalDateTime entryTime;
    private LocalDate date;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, LeaseRepository leaseRepository, EventRepository eventRepository) {
        this.paymentRepository = paymentRepository;
        this.leaseRepository = leaseRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public boolean process(Map<String, Object> events) {
        lease = (Lease) events.get("lease");

        if (Objects.nonNull(lease)) {
            Payment payment = new Payment();
            payment.setPaymentInstat(LocalDateTime.now());
            payment.setLease(lease);
            payment.setValue(RealTime.getPaymentValue(lease));
            
            payment.setDurationTime(RealTime.getDduration(lease));
            
            lease.setPayment(payment);
            
            paymentRepository.save(payment);
            leaseRepository.save(lease);
            composeEvent();
            
        }

        return false;

    }

    private void composeEvent() {

        Event event = new Event();
        event.setEventInstant(Objects.nonNull(entryTime) ? entryTime : LocalDateTime.now());
        event.setLease(lease);
        event.setEventType(EventyType.PAYMENT);
        event.setLicensePlate(lease.getLicensePlate());

        if (Objects.isNull(lease.getEvents()))
            lease.setEvents(new ArrayList<>());

        lease.getEvents().add(event);

        eventRepository.save(event);
        leaseRepository.save(lease);

    }

    public Map<String, Object> revenue(String event) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new LinkedHashMap<>();

        Locale localeBR = new Locale("pt","BR");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeBR);
        
        try {
            JsonNode root = mapper.readTree(event);
            JsonNode dateNode = root.at("/date");
            JsonNode sectorNode = root.at("/sector");
            
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
            date = mapper.convertValue(dateNode, new TypeReference<LocalDate>(){});
            String sector = mapper.convertValue(sectorNode, new TypeReference<String>(){});
            
            List<Lease> leases = leaseRepository.findByEntryTimeBetweenAndCiclo(date.atTime(0, 0, 0), date.atTime(23, 59, 59), Ciclo.CLOSE);
            if (Objects.nonNull(leases)) {
                Integer sum = leases.stream()
                        .filter((l) -> l.getGarage().getSector().equals(sector))
                        .mapToInt(l -> l.getPayment().getValue().intValue())
                        .sum();
                
                response.put("amount", Objects.nonNull(sum) && sum>0 ? format.format(new BigDecimal(sum).setScale(2)): format.format(new BigDecimal(0).setScale(2)));
                response.put("currency", "BRL");
                response.put("timestamp", Objects.nonNull(date) ? date.atTime(0, 0, 0) : "");
                
            }else {
                response.put("amount", format.format(new BigDecimal(0).setScale(2)));
                response.put("currency", "BRL");
                response.put("timestamp", Objects.nonNull(date) ? date.atTime(0, 0, 0) : "");
            }
            
            
        } catch (Exception e) {
            response.put("amount", format.format(new BigDecimal(0).setScale(2)));
            response.put("currency", "BRL");
            response.put("timestamp", Objects.nonNull(date) ? date.atTime(0, 0, 0) : "");
        }
        
        
        return response;
    }

}
