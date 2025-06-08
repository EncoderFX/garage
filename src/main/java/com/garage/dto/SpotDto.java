package com.garage.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SpotDto {

    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("sector")
    private String sector;
    
    @JsonProperty("lat")
    private BigDecimal lat;
    
    @JsonProperty("lng")
    private BigDecimal lng;
    
    @JsonProperty("occupied")
    private Boolean occupied;

}
