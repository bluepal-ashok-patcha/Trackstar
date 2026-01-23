package com.trackstar.vehiclemanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class VehicleManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleManagementServiceApplication.class, args);
    }

}
