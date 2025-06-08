package com.garage.entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Spot {

    @Id
    private Long id;
    private String sector;
    
    @Column(precision=10, scale=6)
    private BigDecimal lat;
    
    @Column(precision=10, scale=6)
    private BigDecimal lng;
    private Boolean occupied;
    
    @ManyToOne
    @JoinColumn(name="garageId", nullable=false)
    private Garage garage;
    
    @OneToMany(mappedBy="spot", fetch = FetchType.EAGER)
    private List<Lease> leases;

}
