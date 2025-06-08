package com.garage.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garage.entity.Garage;

public interface GarageRepository extends JpaRepository<Garage, UUID> {
    

}
