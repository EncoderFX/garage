package com.garage.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garage.entity.Event;
import com.garage.entity.Garage;
import com.garage.entity.Spot;

public interface EventRepository extends JpaRepository<Event, UUID> {

}
