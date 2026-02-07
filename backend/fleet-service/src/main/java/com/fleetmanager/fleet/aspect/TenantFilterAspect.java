package com.fleetmanager.fleet.aspect;

import com.fleetmanager.fleet.context.TenantContext;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TenantFilterAspect {

    private final EntityManager entityManager;

    @Before("execution(* com.fleetmanager..repository.*.*(..))")
    public void enableTenantFilter() {

        Long tenantId = TenantContext.getCurrentTenantId();
        if (tenantId == null) {
            return; // public or auth endpoints
        }

        Session session = entityManager.unwrap(Session.class);

        Filter filter = session.getEnabledFilter("tenantFilter");
        if (filter == null) {
            filter = session.enableFilter("tenantFilter");
        }

        filter.setParameter("tenantId", tenantId);
    }
}
