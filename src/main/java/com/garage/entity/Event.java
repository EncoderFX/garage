package com.garage.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.garage.domain.Ciclo;
import com.garage.domain.EventyType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    
    private String licensePlate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime eventInstant;
    
    @Enumerated(EnumType.STRING)
    private EventyType eventType;
    
    @ManyToOne
    @JoinColumn(name="lease", nullable=false)
    private Lease lease;

}
