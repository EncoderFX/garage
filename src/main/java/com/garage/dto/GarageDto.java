package com.garage.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GarageDto {

    @JsonProperty("sector")
    private String sector;
    
    @JsonProperty("base_price")
    private BigDecimal basePrice;
    
    @JsonProperty("max_capacity")
    private Integer maxCapacity;
    
    @JsonProperty("open_hour")
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonFormat(pattern="HH:mm")
    private LocalTime openHour;
    
    @JsonProperty("close_hour")
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonFormat(pattern="HH:mm")
    private LocalTime closeHour;
    
    @JsonProperty("duration_limit_minutes")
    private Integer durationLimitMinutes;

}
