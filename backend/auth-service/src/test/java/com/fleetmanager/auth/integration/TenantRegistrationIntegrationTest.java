package com.fleetmanager.auth.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@ActiveProfiles("test")
class TenantRegistrationIntegrationTest {

    static {
        System.setProperty("user.timezone", "Asia/Kolkata");
    }

    @Autowired
    private MockMvc mockMvc;

    // ================= PostgreSQL Container =================
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("integration_test_db")
                    .withUsername("testuser")
                    .withPassword("testpass");

    // ================= Inject DB Properties =================
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");

        // disable external infra in tests
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.config.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
    }

    // ================= Test Payloads =================
    private static final String VALID_REQUEST = """
        {
          "organizationName": "Fleet Corporation",
          "subdomain": "fleetcorp",
          "adminEmail": "admin@fleetcorp.com",
          "adminPassword": "StrongPass123"
        }
        """;

    private static final String DUPLICATE_REQUEST = """
        {
          "organizationName": "Another Corp",
          "subdomain": "fleetcorp",
          "adminEmail": "another@corp.com",
          "adminPassword": "StrongPass123"
        }
        """;

    private static final String INVALID_REQUEST = """
        {
          "organizationName": "",
          "subdomain": "",
          "adminEmail": "",
          "adminPassword": ""
        }
        """;

    // ======================================================
    // 1) SUCCESS CASE
    // ======================================================
    @Test
    @Sql(statements = "TRUNCATE TABLE tenants RESTART IDENTITY CASCADE",
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void givenValidRequest_whenRegisterTenant_thenCreated() throws Exception {

        // -------- Given --------
        String request = VALID_REQUEST;

        // -------- When --------
        var result = mockMvc.perform(post("/api/auth/register-tenant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        // -------- Then --------
        result.andExpect(status().isCreated())
              .andExpect(jsonPath("$.tenantId").exists());
    }

    // ======================================================
    // 2) DUPLICATE SUBDOMAIN CASE
    // ======================================================
    @Test
    @Sql(statements = "TRUNCATE TABLE tenants RESTART IDENTITY CASCADE",
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void givenDuplicateSubdomain_whenRegisterTenant_thenConflict() throws Exception {

        // -------- Given --------
        String firstRequest = VALID_REQUEST;
        String duplicateRequest = DUPLICATE_REQUEST;

        // -------- When --------
        // First tenant creation (should be created)
        mockMvc.perform(post("/api/auth/register-tenant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(firstRequest))
                .andExpect(status().isCreated());

        // Second tenant creation with same subdomain
        var result = mockMvc.perform(post("/api/auth/register-tenant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateRequest));

        // -------- Then --------
        // Expect 409 Conflict because subdomain already exists
        result.andExpect(status().isConflict());
    }


    // ======================================================
    // 3) VALIDATION ERROR CASE
    // ======================================================
    @Test
    @Sql(statements = "TRUNCATE TABLE tenants RESTART IDENTITY CASCADE",
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void givenInvalidRequest_whenRegisterTenant_thenBadRequest() throws Exception {

        // -------- Given --------
        String invalidRequest = INVALID_REQUEST;

        // -------- When --------
        var result = mockMvc.perform(post("/api/auth/register-tenant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest));

        // -------- Then --------
        result.andExpect(status().isBadRequest());
    }
}