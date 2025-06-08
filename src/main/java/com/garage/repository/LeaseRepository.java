package com.garage.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garage.domain.Ciclo;
import com.garage.entity.Lease;
import com.garage.entity.Spot;

public interface LeaseRepository extends JpaRepository<Lease, UUID> {

    List<Lease> findByEntryTimeBetweenAndCiclo( LocalDateTime ini, LocalDateTime end, Ciclo ciclo );
    
    List<Lease> findByEntryTimeBetween( LocalDateTime ini, LocalDateTime end );
    
    List<Lease> findByCiclo( Ciclo ciclo );
    
    Lease findBySpotAndCiclo( Spot spot, Ciclo ciclo );
    
    Lease findByLicensePlateAndCiclo( String plate, Ciclo ciclo );
    
    List<Lease> findByEntryTime(LocalDateTime entryTime);
}
