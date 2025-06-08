package com.garage.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.garage.domain.Ciclo;
import com.garage.domain.EventyType;
import com.garage.dto.EventEnterGarageDto;
import com.garage.dto.EventExitGarageDto;
import com.garage.dto.EventSpotDto;
import com.garage.entity.Event;
import com.garage.entity.Lease;
import com.garage.entity.Spot;
import com.garage.repository.EventRepository;
import com.garage.repository.LeaseRepository;
import com.garage.repository.SpotRepository;
import com.garage.util.Middleware;
import com.garage.util.RealTime;

@Service
public class LeaseService extends Middleware {

    private final LeaseRepository leaseRepository;
    private final EventRepository eventRepository;
    private final SpotRepository spotRepository;

    private Map<String, Object> events;
    private String plate;
    private EventyType eventyType = null;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Lease lease;

    @Autowired
    public LeaseService(LeaseRepository leaseRepository, EventRepository eventRepository, SpotRepository spotRepository) {
        this.leaseRepository = leaseRepository;
        this.eventRepository = eventRepository;
        this.spotRepository = spotRepository;
    }

    @Override
    public boolean process(Map<String, Object> events) {
        this.events = events;
        lease = new Lease();
        EventEnterGarageDto eventEnterGarageDto = new EventEnterGarageDto();
        EventExitGarageDto eventExitGarageDto = new EventExitGarageDto();
        EventSpotDto eventSpotDto = new EventSpotDto();

        if (Objects.isNull(events.get("eventDto")))
            return false;

        Integer maxCapacity = (Integer) events.get("maxCapacity");
        List<Lease> leases = leaseRepository.findByCiclo(Ciclo.OPEN);

        if (events.get("eventDto") instanceof EventEnterGarageDto) {
            eventEnterGarageDto = (EventEnterGarageDto) events.get("eventDto");
            plate = eventEnterGarageDto.getLicensePlate();
            eventyType = Enum.valueOf(EventyType.class, eventEnterGarageDto.getEventType());
            lease = leases.stream().filter(l -> l.getLicensePlate().equalsIgnoreCase(plate)).findAny().orElse(null);
            entryTime = eventEnterGarageDto.getEntryTime();
        } else if (events.get("eventDto") instanceof EventSpotDto) {
            eventSpotDto = (EventSpotDto) events.get("eventDto");
            plate = eventSpotDto.getLicensePlate();
            eventyType = Enum.valueOf(EventyType.class, eventSpotDto.getEventType());
            lease = leases.stream().filter(l -> l.getLicensePlate().equalsIgnoreCase(plate)).findAny().orElse(null);
        } else if (events.get("eventDto") instanceof EventExitGarageDto) {
            eventExitGarageDto = (EventExitGarageDto) events.get("eventDto");
            plate = eventExitGarageDto.getLicensePlate();
            eventyType = Enum.valueOf(EventyType.class, eventExitGarageDto.getEventType());
            lease = leases.stream().filter(l -> l.getLicensePlate().equalsIgnoreCase(plate)).findAny().orElse(null);
            exitTime = eventExitGarageDto.getExitTime();
        }else {
            return false;
        }

        if (maxCapacity == leases.size()) {
            System.out.println("Garagem lotada, sem vagas");
            return false;
        } else {

            openLease(eventEnterGarageDto);
            closeLease(eventExitGarageDto);
            composeSpot(eventSpotDto);
            composeEvent();

        }

        events.put("lease", lease);
        return eventyType.equals(EventyType.EXIT) ? next(events) : false;

    }

    private void openLease(EventEnterGarageDto eventGarageDto) {
        if (Objects.isNull(lease)) {
            lease = new Lease();
            lease.setLicensePlate(eventGarageDto.getLicensePlate());
            lease.setEntryTime(eventGarageDto.getEntryTime());
            lease.setCiclo(Ciclo.OPEN);
            leaseRepository.save(lease);
        }
    }

    private void closeLease(EventExitGarageDto eventGarageDto) {
        if (Objects.nonNull(lease) && eventyType.equals(EventyType.EXIT)) {
            lease.setExitTime(exitTime);
            lease.setCiclo(Ciclo.CLOSE);
            leaseRepository.save(lease);
            
            lease.getSpot().setOccupied(false);
            spotRepository.save(lease.getSpot());
        }

    }
    
    private void composeSpot(EventSpotDto eventDto) {
        if (Objects.nonNull(events.get("spot")) && Objects.nonNull(eventDto)) {
            Spot spot = (Spot) events.get("spot");
            lease.setSpot(spot);
            lease.setGarage(spot.getGarage());
            lease.setCalculationRuleCapacity(RealTime.getCalculationRuleCapacity(spotRepository.countByOccupied(true), spotRepository.count()));
            leaseRepository.save(lease);
        }
    }

    private void composeEvent() {

        Event event = new Event();
        event.setEventInstant(Objects.nonNull(entryTime) ? entryTime : LocalDateTime.now());
        event.setLease(lease);
        event.setEventType(eventyType);
        event.setLicensePlate(plate);

        if (Objects.isNull(lease.getEvents()))
            lease.setEvents(new ArrayList<>());

        lease.getEvents().add(event);

        eventRepository.save(event);
        leaseRepository.save(lease);

    }

}
