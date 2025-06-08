package com.garage.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.garage.service.SpotService;

@RestController
public class SpotController {

    private final SpotService spotService;

    @Autowired
    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @PostMapping("/spot-status")
    public ResponseEntity<Object> spotStatus( @RequestBody String event ) {
        var response = spotService.getSpotStatus(event);
        return ResponseEntity.status(Objects.nonNull(response.get("error")) ? HttpStatus.NOT_FOUND : HttpStatus.OK)
                .body(response);
    }   
    
    @PostMapping("/plate-status")
    public ResponseEntity<Object> plateStatus( @RequestBody String event ) {
        var response = spotService.getPlateStatus(event);
        return ResponseEntity.status(Objects.nonNull(response.get("error")) ? HttpStatus.NOT_FOUND : HttpStatus.OK)
                .body(response);
    }   
}
