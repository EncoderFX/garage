package com.garage.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.garage.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    
}
