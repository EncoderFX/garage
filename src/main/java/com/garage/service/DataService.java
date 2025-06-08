package com.garage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garage.dto.GarageDto;
import com.garage.dto.SpotDto;
import com.garage.entity.Garage;
import com.garage.entity.Spot;
import com.garage.repository.GarageRepository;
import com.garage.repository.SpotRepository;

@Service
public class DataService {

    private final GarageRepository garageRepository;
    private final SpotRepository spotRepository;
    

    @Autowired
    public DataService(GarageRepository garageRepository, SpotRepository spotRepository) {
        this.garageRepository = garageRepository;
        this.spotRepository = spotRepository;
    }
    
    public void processDataObjects(String json) {

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(json);
            JsonNode garageNode = root.at("/garage");
            JsonNode spotsNode = root.at("/spots");
            
            List<GarageDto> garagesDto = mapper.convertValue(garageNode, new TypeReference<List<GarageDto>>(){});
            List<SpotDto> spotsDto = mapper.convertValue(spotsNode, new TypeReference<List<SpotDto>>(){});
            
            List<Garage> garages = 
                    garagesDto.stream()
                    .map(this::convertDtoToGarage)
                    .collect(Collectors.toList());
            
            List<Spot> spots = 
                    spotsDto.stream()
                    .map(this::convertDtoToSpot)
                    .collect(Collectors.toList());
            
            saveAll(garages, spots);
            
            System.out.println(garages.size());
            System.out.println(spots.size());

        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }

    }
    
    private void saveAll(List<Garage> garages, List<Spot> spots) {
        garages.forEach(g->{
            g.setSpots(new ArrayList<>());
            spots.forEach(s->{
                if (s.getSector().equalsIgnoreCase(g.getSector()))
                    s.setGarage(g);
                    g.getSpots().add(s);
            });
        });
        
        garageRepository.saveAll(garages);
        spotRepository.saveAll(spots);
        
    }
    
    private Garage convertDtoToGarage(GarageDto dto) {
        Garage garage = new Garage();
        garage.setSector(dto.getSector());
        garage.setDurationLimitMinutes(dto.getDurationLimitMinutes());
        garage.setBasePrice(dto.getBasePrice());
        garage.setMaxCapacity(dto.getMaxCapacity());
        garage.setOpenHour(dto.getOpenHour());
        garage.setCloseHour(dto.getCloseHour());
        return garage;
    }

    private Spot convertDtoToSpot(SpotDto dto) {
        Spot spot = new Spot();
        spot.setId(dto.getId());
        spot.setSector(dto.getSector());
        spot.setLat(dto.getLat());
        spot.setLng(dto.getLng());
        spot.setOccupied(dto.getOccupied());
        return spot;
    }
}
