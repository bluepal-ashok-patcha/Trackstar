package com.fleetmanager.auth.repository;

import com.fleetmanager.auth.entity.TestDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDocumentRepository
        extends JpaRepository<TestDocument, Long> {
}
