CREATE TABLE tenants (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(100) NOT NULL,

    subdomain VARCHAR(50) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT uk_tenants_subdomain UNIQUE (subdomain)
);
