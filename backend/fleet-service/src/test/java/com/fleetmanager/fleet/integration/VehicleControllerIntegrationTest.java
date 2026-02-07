package com.fleetmanager.fleet.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetmanager.fleet.dto.request.VehicleCreateDTO;
import com.fleetmanager.fleet.entity.Vehicle;
import com.fleetmanager.fleet.enums.VehicleStatus;
import com.fleetmanager.fleet.enums.VehicleType;
import com.fleetmanager.fleet.repository.VehicleRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerIntegrationTest {

    // =====================================================
    // 🔥 PostgreSQL Testcontainer
    // =====================================================

    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    static {
        System.setProperty("user.timezone", "Asia/Kolkata"); // ✅ Fix timezone issue
        postgres.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

        registry.add("spring.flyway.enabled", () -> "true");

        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");

        registry.add("jwt.secret",
                () -> "testtesttesttesttesttesttesttesttesttest");
    }

    // =====================================================
    // Dependencies
    // =====================================================

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    VehicleRepository vehicleRepository;

    private Key key;

    @BeforeEach
    void setup() {
        vehicleRepository.deleteAll();
        key = Keys.hmacShaKeyFor(
                "testtesttesttesttesttesttesttesttesttest".getBytes());
    }

    // =====================================================
    // 🔐 JWT Helper
    // =====================================================

    private String generateToken(Long userId, Long tenantId, String role) {
        return Jwts.builder()
                .claim("user_id", userId)
                .claim("tenant_id", tenantId)
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(key)
                .compact();
    }

    private VehicleCreateDTO validDTO(String plate) {
        VehicleCreateDTO dto = new VehicleCreateDTO();
        dto.setLicensePlate(plate);
        dto.setMake("Toyota");
        dto.setModel("Camry");
        dto.setYear(2022);
        dto.setType(VehicleType.CAR);
        return dto;
    }

    // =====================================================
    // ================= POST TESTS ========================
    // =====================================================

    @Test
    @DisplayName("1️⃣ Admin creates vehicle → 201")
    void adminCreatesVehicle_shouldReturn201() throws Exception {

        // GIVEN
        String token = generateToken(1L, 100L, "ADMIN");
        VehicleCreateDTO dto = validDTO("AP09AB1234");

        // WHEN
        var result = mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // THEN
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("2️⃣ Manager creates vehicle → 201")
    void managerCreatesVehicle_shouldReturn201() throws Exception {

        // GIVEN
        String token = generateToken(2L, 100L, "MANAGER");
        VehicleCreateDTO dto = validDTO("AP09AB5678");

        // WHEN
        var result = mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // THEN
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("3️⃣ Driver creates vehicle → 403")
    void driverCreatesVehicle_shouldReturn403() throws Exception {

        // GIVEN
        String token = generateToken(3L, 100L, "DRIVER");
        VehicleCreateDTO dto = validDTO("AP09AB9999");

        // WHEN
        var result = mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // THEN
        result.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("4️⃣ Duplicate license plate → 400")
    void duplicateLicensePlate_shouldReturn400() throws Exception {

        // GIVEN
        String token = generateToken(1L, 100L, "ADMIN");
        VehicleCreateDTO dto = validDTO("DUPL123");

        mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // WHEN
        var result = mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // THEN
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("5️⃣ Validation error → 400")
    void validationError_shouldReturn400() throws Exception {

        // GIVEN
        String token = generateToken(1L, 100L, "ADMIN");
        VehicleCreateDTO invalid = new VehicleCreateDTO();

        // WHEN
        var result = mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)));

        // THEN
        result.andExpect(status().isBadRequest());
    }

    // =====================================================
    // ================= GET TESTS =========================
    // =====================================================

    @Test
    @DisplayName("6️⃣ No filters → return all vehicles")
    void getAllVehicles_noFilters() throws Exception {

        // GIVEN
        String token = generateToken(1L, 100L, "ADMIN");
        mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDTO("ALL123"))));

        // WHEN
        var result = mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer " + token));

        // THEN
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("7️⃣ Filter by status=AVAILABLE")
    void filterByStatus_available() throws Exception {

        // GIVEN
        String token = generateToken(1L, 100L, "ADMIN");

        mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDTO("STAT123"))));

        // WHEN
        var result = mockMvc.perform(get("/api/vehicles")
                .param("status", "AVAILABLE")
                .header("Authorization", "Bearer " + token));

        // THEN
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.content[0].status")
              .value("AVAILABLE"));
    }

    @Test
    @DisplayName("8️⃣ Search by license plate")
    void searchByLicensePlate() throws Exception {

        // GIVEN
        String token = generateToken(1L, 100L, "ADMIN");

        mockMvc.perform(post("/api/vehicles")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDTO("SEARCH123"))));

        // WHEN
        var result = mockMvc.perform(get("/api/vehicles")
                .param("search", "SEARCH")
                .header("Authorization", "Bearer " + token));

        // THEN
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.content[0].licensePlate")
              .value("SEARCH123"));
    }

    @Test
    @DisplayName("9️⃣ Pagination works")
    void paginationWorks() throws Exception {

        // GIVEN
        for (int i = 0; i < 25; i++) {
            Vehicle vehicle = Vehicle.builder()
                    .licensePlate("PAGE" + i)
                    .make("Toyota")
                    .model("Camry")
                    .year(2022)
                    .type(VehicleType.CAR)
                    .status(VehicleStatus.AVAILABLE)
                    .createdAt(LocalDateTime.now())
                    .build();
            vehicle.setTenantId(100L);
            vehicleRepository.save(vehicle);
        }

        String token = generateToken(1L, 100L, "ADMIN");

        // WHEN
        var result = mockMvc.perform(get("/api/vehicles")
                .param("size", "10")
                .header("Authorization", "Bearer " + token));

        // THEN
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.content.length()")
              .value(10));
    }

    @Test
    @DisplayName("🔟 Multi-tenancy → only current tenant vehicles")
    void multiTenancy_shouldOnlyReturnCurrentTenantVehicles() throws Exception {

        // GIVEN
        Vehicle t1 = Vehicle.builder()
                .licensePlate("TENANT1")
                .make("Ford")
                .model("F150")
                .year(2020)
                .type(VehicleType.TRUCK)
                .status(VehicleStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .build();
        t1.setTenantId(100L);
        vehicleRepository.save(t1);

        Vehicle t2 = Vehicle.builder()
                .licensePlate("TENANT2")
                .make("Ford")
                .model("F150")
                .year(2020)
                .type(VehicleType.TRUCK)
                .status(VehicleStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .build();
        t2.setTenantId(200L);
        vehicleRepository.save(t2);

        String token = generateToken(1L, 100L, "ADMIN");

        // WHEN
        var result = mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer " + token));

        // THEN
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.content.length()").value(1))
              .andExpect(jsonPath("$.content[0].licensePlate")
              .value("TENANT1"));
    }
}