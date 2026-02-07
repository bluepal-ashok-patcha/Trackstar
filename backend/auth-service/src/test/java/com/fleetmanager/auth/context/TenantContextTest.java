package com.fleetmanager.auth.context;

import com.fleetmanager.auth.exception.TenantContextMissingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TenantContextTest {

    @AfterEach
    void tearDown() {
        // Important cleanup to avoid thread pollution
        TenantContext.clear();
    }

    @Test
    void shouldSetAndGetTenantId() {
        // given
        Long tenantId = 1L;

        // when
        TenantContext.setCurrentTenantId(tenantId);

        // then
        assertEquals(tenantId, TenantContext.getCurrentTenantId());
    }

    @Test
    void shouldClearTenantId() {
        // given
        TenantContext.setCurrentTenantId(1L);

        // when
        TenantContext.clear();

        // then
        assertNull(TenantContext.getCurrentTenantId());
    }

    @Test
    void shouldThrowExceptionWhenTenantIdIsMissing() {
        // given
        TenantContext.clear();

        // then
        assertThrows(
                TenantContextMissingException.class,
                TenantContext::getCurrentTenantIdOrThrow
        );
    }

    @Test
    void shouldReturnTenantIdWhenPresentUsingOrThrow() {
        // given
        Long tenantId = 99L;
        TenantContext.setCurrentTenantId(tenantId);

        // when
        Long result = TenantContext.getCurrentTenantIdOrThrow();

        // then
        assertEquals(tenantId, result);
    }

    @Test
    void shouldIsolateTenantContextBetweenThreads() throws InterruptedException {
        // holders to capture thread results
        final Long[] thread1Tenant = new Long[1];
        final Long[] thread2Tenant = new Long[1];

        Thread thread1 = new Thread(() -> {
            TenantContext.setCurrentTenantId(1L);
            thread1Tenant[0] = TenantContext.getCurrentTenantId();
            TenantContext.clear();
        });

        Thread thread2 = new Thread(() -> {
            TenantContext.setCurrentTenantId(2L);
            thread2Tenant[0] = TenantContext.getCurrentTenantId();
            TenantContext.clear();
        });

        // when
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // then
        assertEquals(1L, thread1Tenant[0]);
        assertEquals(2L, thread2Tenant[0]);
    }
}