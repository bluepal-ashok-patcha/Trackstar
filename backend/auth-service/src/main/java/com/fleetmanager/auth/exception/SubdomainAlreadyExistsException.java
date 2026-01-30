package com.fleetmanager.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SubdomainAlreadyExistsException extends RuntimeException {
    public SubdomainAlreadyExistsException(String message) {
        super(message);
    }
}