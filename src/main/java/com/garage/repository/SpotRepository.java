package com.garage.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garage.entity.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    
    Spot findByLatAndLng(BigDecimal lat, BigDecimal lng);
    
    Long countByOccupied(Boolean occupied);
    
}
