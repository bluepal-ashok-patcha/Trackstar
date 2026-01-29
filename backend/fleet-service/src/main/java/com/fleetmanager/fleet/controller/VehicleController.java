package com.fleetmanager.fleet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @GetMapping("/test")
    public String test() {
        return "Vehicle Service is reachable!";
    }
    // ... existing code ...
}