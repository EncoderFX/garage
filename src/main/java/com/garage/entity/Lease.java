package com.garage.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.garage.domain.Ciclo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
public class Lease {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    
    private String licensePlate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime entryTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime exitTime;
    
    private BigDecimal calculationRuleCapacity;
    
    @Enumerated(EnumType.STRING)
    private Ciclo ciclo;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "garageId", referencedColumnName = "id")
    private Garage garage;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "spotId", referencedColumnName = "id")
    private Spot spot;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "paymentId", referencedColumnName = "id")
    private Payment payment;
    
    @OneToMany(mappedBy="lease", fetch = FetchType.EAGER)
    private List<Event> events;

}
