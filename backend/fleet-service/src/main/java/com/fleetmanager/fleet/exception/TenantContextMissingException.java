package com.fleetmanager.fleet.exception;

public class TenantContextMissingException extends RuntimeException {
    public TenantContextMissingException() {
        super("Tenant context is missing for the current request");
    }

    public TenantContextMissingException(String message) {
        super(message);
    }
}
