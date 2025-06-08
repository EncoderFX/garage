package com.garage.entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    private String sector;
    private BigDecimal basePrice;
    private Integer maxCapacity;
    private LocalTime openHour;
    private LocalTime closeHour;
    private Integer durationLimitMinutes;
    
    @OneToMany(mappedBy="garage", fetch = FetchType.EAGER)
    private List<Spot> spots;
    
    @OneToMany(mappedBy="garage", fetch = FetchType.EAGER)
    private List<Lease> leases;
}
