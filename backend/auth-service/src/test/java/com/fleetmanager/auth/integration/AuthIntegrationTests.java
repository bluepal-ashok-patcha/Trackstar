package com.fleetmanager.auth.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetmanager.auth.config.PostgresContainerConfig;
import com.fleetmanager.auth.context.TenantContext;
import com.fleetmanager.auth.entity.Tenant;
import com.fleetmanager.auth.entity.User;
import com.fleetmanager.auth.enums.Role;
import com.fleetmanager.auth.enums.UserStatus;
import com.fleetmanager.auth.repository.TenantRepository;
import com.fleetmanager.auth.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthIntegrationTests extends PostgresContainerConfig {

	static {
		System.setProperty("user.timezone", "Asia/Kolkata");
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TenantRepository tenantRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// ================= Test Constants =================
	private static final String SUBDOMAIN = "fleetcorp";
	private static final String EMAIL_VALID = "admin@fleetcorp.com";
	private static final String PASSWORD_VALID = "StrongPass123";
	private static final String PASSWORD_WRONG = "WrongPassword999";

	private Tenant testTenant;

	// ======================================================
	// TEST DATA SETUP
	// ======================================================
	@BeforeEach
	void setUp() {
	    userRepository.deleteAll();
	    tenantRepository.deleteAll();

	    // Create tenant
	    testTenant = new Tenant();
	    testTenant.setName("Fleet Corporation");
	    testTenant.setSubdomain(SUBDOMAIN);
	    testTenant.setActive(true);

	    testTenant = tenantRepository.saveAndFlush(testTenant);

	    // 🔑 Set tenant context BEFORE creating users
	    TenantContext.setCurrentTenantId(testTenant.getId());

	    // Active admin
	    User activeAdmin = new User();
	    activeAdmin.setEmail(EMAIL_VALID);
	    activeAdmin.setPasswordHash(passwordEncoder.encode(PASSWORD_VALID));
	    activeAdmin.setName("Fleet Admin");
	    activeAdmin.setRole(Role.ADMIN);
	    activeAdmin.setStatus(UserStatus.ACTIVE);

	    userRepository.saveAndFlush(activeAdmin);

	    // Inactive user
	    User inactiveUser = new User();
	    inactiveUser.setEmail("inactive@fleetcorp.com");
	    inactiveUser.setPasswordHash(passwordEncoder.encode(PASSWORD_VALID));
	    inactiveUser.setName("Inactive User");
	    inactiveUser.setRole(Role.DRIVER);
	    inactiveUser.setStatus(UserStatus.INACTIVE);

	    userRepository.saveAndFlush(inactiveUser);

	    // Optional but good hygiene
	    TenantContext.clear();
	}


	// ======================================================
	// 1) SUCCESS CASE
	// ======================================================
	@Test
	@DisplayName("Should successfully login with valid credentials and return JWT")
	void login_givenValidCredentials_returnsToken() throws Exception {

		// ===== GIVEN =====
		String loginJson = """
				{
				  "subdomain": "%s",
				  "email": "%s",
				  "password": "%s"
				}
				""".formatted(SUBDOMAIN, EMAIL_VALID, PASSWORD_VALID);

		// ===== WHEN & THEN =====
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
				.andExpect(status().isOk()).andExpect(jsonPath("$.token", notNullValue()))
				.andExpect(jsonPath("$.userId", notNullValue())).andExpect(jsonPath("$.email", is(EMAIL_VALID)))
				.andExpect(jsonPath("$.role", is("ADMIN")))
				.andExpect(jsonPath("$.tenantId", is(testTenant.getId().intValue())));
	}

	// ======================================================
	// 2) WRONG PASSWORD
	// ======================================================
	@Test
	@DisplayName("Should return 401 when password is incorrect")
	void login_givenWrongPassword_returns401() throws Exception {

		// ===== GIVEN =====
		String loginJson = """
				{
				  "subdomain": "%s",
				  "email": "%s",
				  "password": "%s"
				}
				""".formatted(SUBDOMAIN, EMAIL_VALID, PASSWORD_WRONG);

		// ===== WHEN & THEN =====
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
				.andExpect(status().isUnauthorized());
	}

	// ======================================================
	// 3) NON-EXISTENT USER
	// ======================================================
	@Test
	@DisplayName("Should return 401 when user does not exist")
	void login_givenUnknownUser_returns401() throws Exception {

		// ===== GIVEN =====
		String loginJson = """
				{
				  "subdomain": "%s",
				  "email": "unknown@fleetcorp.com",
				  "password": "%s"
				}
				""".formatted(SUBDOMAIN, PASSWORD_VALID);

		// ===== WHEN & THEN =====
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
				.andExpect(status().isUnauthorized());
	}

	// ======================================================
	// 4) INACTIVE USER
	// ======================================================
	@Test
	@DisplayName("Should return 401 when user account is inactive")
	void login_givenInactiveUser_returns401() throws Exception {

		// ===== GIVEN =====
		String loginJson = """
				{
				  "subdomain": "%s",
				  "email": "inactive@fleetcorp.com",
				  "password": "%s"
				}
				""".formatted(SUBDOMAIN, PASSWORD_VALID);

		// ===== WHEN & THEN =====
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
				.andExpect(status().isUnauthorized());
	}

	// ======================================================
	// 5) NON-EXISTENT SUBDOMAIN
	// ======================================================
	@Test
	@DisplayName("Should return 401 when subdomain does not exist")
	void login_givenInvalidSubdomain_returns401() throws Exception {

		// ===== GIVEN =====
		String loginJson = """
				{
				  "subdomain": "nonexistentcompany",
				  "email": "%s",
				  "password": "%s"
				}
				""".formatted(EMAIL_VALID, PASSWORD_VALID);

		// ===== WHEN & THEN =====
		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
				.andExpect(status().isUnauthorized());
	}
}