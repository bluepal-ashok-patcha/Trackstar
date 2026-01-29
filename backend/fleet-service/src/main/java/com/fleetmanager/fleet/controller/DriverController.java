package com.fleetmanager.fleet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @GetMapping("/test")
    public String test() {
        return "Driver Service is reachable!";
    }
    // ... existing code ...
}