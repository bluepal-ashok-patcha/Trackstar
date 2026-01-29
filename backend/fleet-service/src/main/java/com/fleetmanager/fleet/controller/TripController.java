package com.fleetmanager.fleet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @GetMapping("/test")
    public String test() {
        return "Trip Service is reachable!";
    }
    // ... existing code ...
}