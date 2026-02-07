CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,

    tenant_id BIGINT NOT NULL,

    license_plate VARCHAR(20) NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INTEGER NOT NULL,
    vin VARCHAR(50),

    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',

    image_url VARCHAR(255),
    odometer_reading INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_vehicles_tenant_license
        UNIQUE (tenant_id, license_plate)
);
