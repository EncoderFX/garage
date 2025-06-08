package com.garage.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EventSpotDto {

    @JsonProperty("license_plate")
    private String licensePlate;
   
    @JsonProperty("lat")
    private BigDecimal lat;
    
    @JsonProperty("lng")
    private BigDecimal lng;
    
    @JsonProperty("event_type")
    private String eventType;
        
}
