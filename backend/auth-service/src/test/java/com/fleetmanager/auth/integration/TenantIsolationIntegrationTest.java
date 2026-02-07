package com.fleetmanager.auth.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fleetmanager.auth.config.PostgresContainerConfig;
import com.fleetmanager.auth.context.TenantContext;
import com.fleetmanager.auth.entity.Tenant;
import com.fleetmanager.auth.entity.TestDocument;
import com.fleetmanager.auth.entity.User;
import com.fleetmanager.auth.enums.Role;
import com.fleetmanager.auth.repository.TenantRepository;
import com.fleetmanager.auth.repository.TestDocumentRepository;
import com.fleetmanager.auth.repository.UserRepository;
import com.fleetmanager.auth.service.AuthService;
import com.fleetmanager.auth.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc  
class TenantIsolationIntegrationTest extends PostgresContainerConfig {

    @Autowired
    private TestDocumentRepository testDocumentRepository;

    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthService authService;

    @Test
    void saveWithTenantA_queryWithTenantB_shouldReturnNoResults() {

        // =========================
        // GIVEN: Two tenants exist
        // =========================
        Tenant tenantA = tenantRepository.save(
                Tenant.builder()
                        .name("Tenant A")
                        .subdomain("tenant-a")
                        .active(true)
                        .build()
        );

        Tenant tenantB = tenantRepository.save(
                Tenant.builder()
                        .name("Tenant B")
                        .subdomain("tenant-b")
                        .active(true)
                        .build()
        );

        // =========================
        // WHEN: Save under Tenant A
        // =========================
        TenantContext.setCurrentTenantId(tenantA.getId());

        TestDocument doc = new TestDocument();
        doc.setName("Tenant A Document");

        testDocumentRepository.saveAndFlush(doc);

        // =========================
        // THEN: Switch to Tenant B
        // =========================
        TenantContext.setCurrentTenantId(tenantB.getId());

        List<TestDocument> result = testDocumentRepository.findAll();

        // =========================
        // ASSERT: No leakage
        // =========================
        assertThat(result).isEmpty();

        // =========================
        // CLEANUP
        // =========================
        TenantContext.clear();
    }
    
    @Test
    void usersAreIsolatedBetweenTenants() {

        Tenant tenantA = tenantRepository.save(
                Tenant.builder().name("Tenant A").subdomain("tenant-a").active(true).build()
        );

        Tenant tenantB = tenantRepository.save(
                Tenant.builder().name("Tenant B").subdomain("tenant-b").active(true).build()
        );

        // Tenant A
        TenantContext.setCurrentTenantId(tenantA.getId());

        User userA = new User();
        userA.setEmail("admin@corp.com");
        userA.setName("Admin A");
        userA.setRole(Role.ADMIN);
        userA.setPasswordHash("pass");

        userRepository.saveAndFlush(userA);

        // Tenant B
        TenantContext.setCurrentTenantId(tenantB.getId());

        User userB = new User();
        userB.setEmail("admin@corp.com");
        userB.setName("Admin B");
        userB.setRole(Role.ADMIN);
        userB.setPasswordHash("pass");

        userRepository.saveAndFlush(userB);

        // Verify isolation
        TenantContext.setCurrentTenantId(tenantA.getId());
        assertThat(userRepository.findAll()).hasSize(1);
        assertThat(userRepository.findAll().get(0).getName()).isEqualTo("Admin A");

        TenantContext.setCurrentTenantId(tenantB.getId());
        assertThat(userRepository.findAll()).hasSize(1);
        assertThat(userRepository.findAll().get(0).getName()).isEqualTo("Admin B");

        TenantContext.clear();
    }

    // =========================================================
    // TEST 2: Cross-Tenant OVERRIDE is BLOCKED
    // =========================================================
    @Test
    void crossTenantOverrideShouldBeBlocked() {

        Tenant tenantA = tenantRepository.save(
                Tenant.builder().name("Tenant A").subdomain("tenant-a").active(true).build()
        );

        Tenant tenantB = tenantRepository.save(
                Tenant.builder().name("Tenant B").subdomain("tenant-b").active(true).build()
        );

        TenantContext.setCurrentTenantId(tenantA.getId());

        User user = new User();
        user.setEmail("hack@test.com");
        user.setName("Hacker");
        user.setRole(Role.ADMIN);
        user.setPasswordHash("pass");

        userRepository.saveAndFlush(user);

        // 🚨 Actual attack attempt
        assertThatThrownBy(() -> user.setTenantId(tenantB.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cross-tenant");

        TenantContext.clear();
    }
    
    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }
    
    // =====================================================
    // TEST 1
    // =====================================================
    // GIVEN  : Valid JWT with tenantId
    // WHEN   : Secured endpoint is accessed
    // THEN   : TenantContext is available during request
    //          AND cleared after request
    // =====================================================
    @Test
    void validJwt_shouldSetTenantContextDuringRequest_andClearAfter() throws Exception {

        // ---------- GIVEN ----------
        Claims claims = Jwts.claims();
        claims.setExpiration(new Date(System.currentTimeMillis() + 60_000));
        claims.put("tenant_id", 100L);

        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractClaims(anyString())).thenReturn(claims);
        when(jwtUtil.extractTenantId(anyString())).thenReturn(100L);

        // ---------- WHEN ----------
        mockMvc.perform(
                get("/api/auth/secured-test")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token")
        )
        // ---------- THEN ----------
        .andExpect(status().isOk())
        .andExpect(content().string("TENANT=100"));

        // AFTER request → ThreadLocal cleared
        assertThat(TenantContext.getCurrentTenantId()).isNull();
    }

    // =====================================================
    // TEST 2
    // =====================================================
    // GIVEN  : Missing JWT
    // WHEN   : Secured endpoint is accessed
    // THEN   : Request rejected (401)
    //          AND TenantContext not set
    // =====================================================
    @Test
    void missingJwt_shouldReturn401_andNotSetTenantContext() throws Exception {

        mockMvc.perform(get("/api/auth/secured-test"))
                .andExpect(status().isUnauthorized());

        assertThat(TenantContext.getCurrentTenantId()).isNull();
    }

    // =====================================================
    // TEST 3
    // =====================================================
    // GIVEN  : Invalid JWT
    // WHEN   : Secured endpoint is accessed
    // THEN   : Request rejected (401)
    //          AND TenantContext not set
    // =====================================================
    @Test
    void invalidJwt_shouldReturn401_andNotSetTenantContext() throws Exception {

        Claims claims = Jwts.claims();
        claims.setExpiration(new Date(System.currentTimeMillis() - 60_000)); // expired

        when(jwtUtil.extractClaims(anyString())).thenReturn(claims);
        when(jwtUtil.validateToken(anyString())).thenReturn(false);

        mockMvc.perform(
                get("/api/auth/secured-test")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.jwt.token")
        )
        .andExpect(status().isUnauthorized());

        assertThat(TenantContext.getCurrentTenantId()).isNull();
    }

    // =====================================================
    // TEST 4
    // =====================================================
    // GIVEN  : JWT without tenantId
    // WHEN   : Secured endpoint is accessed
    // THEN   : Request rejected (401)
    //          AND TenantContext not set
    // =====================================================
    @Test
    void jwtWithoutTenantId_shouldReturn401_andNotSetTenantContext() throws Exception {

        Claims claims = Jwts.claims();
        claims.setExpiration(new Date(System.currentTimeMillis() + 60_000));

        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractClaims(anyString())).thenReturn(claims);
        when(jwtUtil.extractTenantId(anyString())).thenReturn(null);

        mockMvc.perform(
                get("/api/auth/secured-test")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer jwt.without.tenant")
        )
        .andExpect(status().isUnauthorized());

        assertThat(TenantContext.getCurrentTenantId()).isNull();
    }

}
