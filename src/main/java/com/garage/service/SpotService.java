package com.garage.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garage.domain.Ciclo;
import com.garage.entity.Lease;
import com.garage.entity.Spot;
import com.garage.repository.LeaseRepository;
import com.garage.repository.SpotRepository;
import com.garage.util.RealTime;

@Service
public class SpotService {

    private final SpotRepository spotRepository;
    private final LeaseRepository leaseRepository;
    private Lease lease;
    Locale localeBR = new Locale("pt","BR");
    NumberFormat format = NumberFormat.getCurrencyInstance(localeBR);

    @Autowired
    public SpotService(SpotRepository spotRepository, LeaseRepository leaseRepository) {
        this.spotRepository = spotRepository;
        this.leaseRepository = leaseRepository;
    }

    public Map<String, Object> getSpotStatus(String event){
       
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new LinkedHashMap<>();
        Spot spot = new Spot();
        
        try {
            JsonNode root = mapper.readTree(event);
            JsonNode latNode = root.at("/lat");
            JsonNode lngNode = root.at("/lng");
            
            BigDecimal lat = mapper.convertValue(latNode, new TypeReference<BigDecimal>(){});
            BigDecimal lng = mapper.convertValue(lngNode, new TypeReference<BigDecimal>(){});
            
            spot = spotRepository.findByLatAndLng(lat, lng);
            lease = leaseRepository.findBySpotAndCiclo(spot, Ciclo.OPEN);
            if (Objects.nonNull(lease))
                lease.setCalculationRuleCapacity(RealTime.getCalculationRuleCapacity(spotRepository.countByOccupied(true), spotRepository.count()));
        
        } catch (Exception e) {
            System.out.println("Falha na conversão de lat e lng");
        }

        if (Objects.isNull(spot)) {
            response.put("error", "Não existe spot para a localização informada");
            return response;
        }
        
        
        response.put("ocupied", spot.getOccupied());
        response.put("license_plate", Objects.nonNull(lease) ? lease.getLicensePlate() : "");
        response.put("price_until_now", format.format(RealTime.getPaymentValue(lease)));
        response.put("entry_time", Objects.nonNull(lease) ? lease.getEntryTime() : "");
        response.put("time_parked", Objects.nonNull(lease) ? RealTime.getDuration(lease) : "");

        return response;
    }
    
    
public Map<String, Object> getPlateStatus(String event){
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            JsonNode root = mapper.readTree(event);
            JsonNode plateNode = root.at("/license_plate");
            
            String plate = mapper.convertValue(plateNode, new TypeReference<String>(){});
            lease = leaseRepository.findByLicensePlateAndCiclo(plate, Ciclo.OPEN);
            if (Objects.nonNull(lease))
                lease.setCalculationRuleCapacity(RealTime.getCalculationRuleCapacity(spotRepository.countByOccupied(true), spotRepository.count()));
            
        } catch (Exception e) {
            System.out.println("Falha na conversão de lat e lng");
        }

        if (Objects.isNull(lease)) {
            response.put("error", "Esta plate não esta na garage");
            return response;
        }
        
        response.put("license_plate", Objects.nonNull(lease) ? lease.getLicensePlate() : "");
        response.put("price_until_now", format.format(RealTime.getPaymentValue(lease)));
        response.put("entry_time", Objects.nonNull(lease) ? lease.getEntryTime() : "");
        response.put("time_parked", Objects.nonNull(lease) ? RealTime.getDuration(lease) : "");
        response.put("lat", Objects.nonNull(lease.getSpot()) ? lease.getSpot().getLat() : 0.00);
        response.put("lng", Objects.nonNull(lease.getSpot()) ? lease.getSpot().getLng() : 0.00);

        return response;
    }

}
