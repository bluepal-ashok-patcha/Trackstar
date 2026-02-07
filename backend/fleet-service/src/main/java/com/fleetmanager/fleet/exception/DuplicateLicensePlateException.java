package com.fleetmanager.fleet.exception;

public class DuplicateLicensePlateException extends RuntimeException {

    public DuplicateLicensePlateException(String licensePlate) {
        super("Vehicle with license plate already exists: " + licensePlate);
    }
}

