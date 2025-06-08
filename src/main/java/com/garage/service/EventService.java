package com.garage.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garage.dto.EventEnterGarageDto;
import com.garage.dto.EventExitGarageDto;
import com.garage.dto.EventSpotDto;
import com.garage.entity.Garage;
import com.garage.entity.Spot;
import com.garage.repository.EventRepository;
import com.garage.repository.GarageRepository;
import com.garage.repository.SpotRepository;
import com.garage.util.Middleware;

@Service
public class EventService extends Middleware {

    private final EventRepository eventRepository;
    private final GarageRepository garageRepository;
    private final SpotRepository spotRepository;
    

    @Autowired
    public EventService(EventRepository eventRepository, GarageRepository garageRepository, SpotRepository spotRepository) {
        this.eventRepository = eventRepository;
        this.garageRepository = garageRepository;
        this.spotRepository = spotRepository;
    }

    @Override
    public boolean process(Map<String, Object> events) {
        ObjectMapper mapper = new ObjectMapper();
        String event = (String) events.get("originalEvent");
        if (Objects.nonNull(event)) {
            try {
                JsonNode root = mapper.readTree(event);
                EventEnterGarageDto eventDto = mapper.convertValue(root, new TypeReference<EventEnterGarageDto>() {});

                events.put("eventDto", eventDto);
                events.put("maxCapacity", getMaxCapacity());
                
                return next(events);
            } catch (Exception e) {
                System.out.println("Próximo chain");
            }
            
            try {
                JsonNode root = mapper.readTree(event);
                EventExitGarageDto eventDto = mapper.convertValue(root, new TypeReference<EventExitGarageDto>() {});

                events.put("eventDto", eventDto);
                events.put("maxCapacity", getMaxCapacity());
                
                return next(events);
            } catch (Exception e) {
                System.out.println("Próximo chain");
            }
            
            try {
                JsonNode root = mapper.readTree(event);
                EventSpotDto eventDto = mapper.convertValue(root, new TypeReference<EventSpotDto>() {});
                Spot spot = spotRepository.findByLatAndLng(eventDto.getLat(), eventDto.getLng());
                
                if (Objects.nonNull(spot) && !spot.getOccupied()) {
                    spot.setOccupied(true);
                    spotRepository.save(spot);
                    System.out.println(String.format("Vaga disponivel [lat: %s, lng: %s]", spot.getLat(), spot.getLng()));
                }

                events.put("spot", spot);
                events.put("eventDto", eventDto);
                events.put("maxCapacity", getMaxCapacity());
                return next(events);
            } catch (Exception e) {
                System.out.println("Próximo chain");
            }
        }

        events.put("error", "Event não identificado");
        return false;

    }
    
    private Integer getMaxCapacity() {
        List<Garage> sectors = garageRepository.findAll();
        return sectors.stream()
                .map(s -> s.getMaxCapacity())
                .reduce(0, Integer::sum);
    }

}
