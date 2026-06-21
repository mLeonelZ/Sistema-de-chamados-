package com.helpdesk.repository;

import com.helpdesk.model.SlaPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SlaPolicyRepository extends JpaRepository<SlaPolicy, UUID> {
    Optional<SlaPolicy> findByNameIgnoreCase(String name);
}
