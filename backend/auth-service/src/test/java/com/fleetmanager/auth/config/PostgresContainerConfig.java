package com.fleetmanager.auth.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class PostgresContainerConfig {

	@Container
	private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:15-alpine")
			.withDatabaseName("fleet_test_db").withUsername("test").withPassword("test");

	static {
		POSTGRES_CONTAINER.start();
	}

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {

		// -------- DATASOURCE --------
		registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);

		registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);

		registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);

		registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

		// -------- JPA --------
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
		registry.add("spring.jpa.show-sql", () -> "true");
		registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");

		// -------- FLYWAY --------
		registry.add("spring.flyway.enabled", () -> "true");
		registry.add("spring.flyway.locations", () -> "classpath:db/migration");

		// =====================================
		// 🚀 DISABLE EUREKA COMPLETELY
		// =====================================
		registry.add("eureka.client.enabled", () -> "false");
		registry.add("eureka.client.register-with-eureka", () -> "false");
		registry.add("eureka.client.fetch-registry", () -> "false");

		registry.add("spring.cloud.discovery.enabled", () -> "false");
		registry.add("spring.cloud.service-registry.auto-registration.enabled", () -> "false");

		// Optional: disable config server if present
		registry.add("spring.cloud.config.enabled", () -> "false");
	}
}